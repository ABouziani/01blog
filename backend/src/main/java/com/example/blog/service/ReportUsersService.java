package com.example.blog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.blog.dto.NewReportRequest;
import com.example.blog.exceptions.DuplicateReportException;
import com.example.blog.model.ReportUsers;
import com.example.blog.model.User;
import com.example.blog.repository.ReportUserRepository;

;

@Service
public class ReportUsersService {

    @Autowired
    private ReportUserRepository reportUserRepository;

    @Autowired
    private UserService userService;

    public List<ReportUsers> getAllReports() {
        return reportUserRepository.findAll();
    }

    public ResponseEntity<?> addReport(NewReportRequest newReportRequest, Long creatorId) {
        if (Objects.equals(newReportRequest.getReportedId(), creatorId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot report yourself!");
        }

        if (reportUserRepository.existsByReporterIdAndReportedId(creatorId, newReportRequest.getReportedId())) {
            throw new DuplicateReportException();
        }

        Optional<User> userOpt = userService.findById(creatorId);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("Reporter user not found", HttpStatus.BAD_REQUEST);
        }
        User reporter = userOpt.get();

        Optional<User> reportedOpt = userService.findById(newReportRequest.getReportedId());
        if (reportedOpt.isEmpty()) {
            return new ResponseEntity<>("Reported user not found", HttpStatus.BAD_REQUEST);
        }
        User reported = reportedOpt.get();

        ReportUsers newReport = new ReportUsers();
        newReport.setReporter(reporter);
        newReport.setReported(reported);
        newReport.setReason(newReportRequest.getReason());
        newReport.setCreatedAt(LocalDateTime.now());
        reportUserRepository.save(newReport);
        return ResponseEntity.ok(newReport);
    }
}
