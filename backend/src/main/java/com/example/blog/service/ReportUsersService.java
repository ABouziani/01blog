package com.example.blog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    public ReportUsers addReport(NewReportRequest newReportRequest, Long creatorId) {
        if (Objects.equals(newReportRequest.getReportedId(), creatorId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot report yourself!");
        }
        
        if (reportUserRepository.existsByReporterIdAndReportedId(creatorId, newReportRequest.getReportedId())) {
            throw new DuplicateReportException();
        }

        User reporter = userService.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Reporter user not found"));

        User reported = userService.findById(newReportRequest.getReportedId())
                .orElseThrow(() -> new RuntimeException("Reported user not found"));

        ReportUsers newReport = new ReportUsers();
        newReport.setReporter(reporter);
        newReport.setReported(reported);
        newReport.setReason(newReportRequest.getReason());
        newReport.setCreatedAt(LocalDateTime.now());
        return reportUserRepository.save(newReport);
    }
}
