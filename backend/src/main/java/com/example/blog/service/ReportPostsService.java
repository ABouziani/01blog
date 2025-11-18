package com.example.blog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.blog.dto.NewReportPostRequest;
import com.example.blog.exceptions.DuplicateReportException;
import com.example.blog.model.Post;
import com.example.blog.model.ReportPosts;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.ReportPostsRepository;

;

@Service
public class ReportPostsService {

    @Autowired
    private ReportPostsRepository reportPostsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostRepository postRepository;

    public List<ReportPosts> getAllReports() {
        return reportPostsRepository.findAll();
    }

    public ReportPosts addReport(NewReportPostRequest newReportPostRequest, Long creatorId) {

        if (reportPostsRepository.existsByReporterIdAndReportedpostId(creatorId, newReportPostRequest.getReportedPostId())) {
            throw new DuplicateReportException();
        }

        User reporter = userService.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Reporter user not found"));

        Post reportedPost = postRepository.findById(newReportPostRequest.getReportedPostId())
                .orElseThrow(() -> new RuntimeException("Reported Post not found"));

        if (Objects.equals(reportedPost.getCreator().getId(), creatorId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot report your posts!");
        }

        ReportPosts newReport = new ReportPosts();
        newReport.setReporter(reporter);
        newReport.setReportedPost(reportedPost);
        newReport.setReason(newReportPostRequest.getReason());
        newReport.setCreatedAt(LocalDateTime.now());
        return reportPostsRepository.save(newReport);
    }
}
