package com.example.blog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.blog.dto.NewReportPostRequest;
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

    public ResponseEntity<?> addReport(NewReportPostRequest newReportPostRequest, Long creatorId) {
        var len = newReportPostRequest.getReason().length();
         if (len < 10 || len > 300) {
            return new ResponseEntity<>("Report reason must be between 10 and 300 characters!", HttpStatus.BAD_REQUEST); 
        }

        if (reportPostsRepository.existsByReporterIdAndReportedpostId(creatorId, newReportPostRequest.getReportedPostId())) {
            return new ResponseEntity<>("You already reported this post!", HttpStatus.BAD_REQUEST);
        }

        Optional<User> reporterOpt = userService.findById(creatorId);
        if (reporterOpt.isEmpty()) {
            return new ResponseEntity<>("Reporter user not found", HttpStatus.BAD_REQUEST);
        }

        User reporter = reporterOpt.get();

        Optional<Post> reportedPostOpt = postRepository.findByIdAndIsHiddenFalse(newReportPostRequest.getReportedPostId());
         if (reportedPostOpt.isEmpty()) {
            return new ResponseEntity<>("Reported Post not found", HttpStatus.BAD_REQUEST);
        }

        Post reportedPost = reportedPostOpt.get();

        if (Objects.equals(reportedPost.getCreator().getId(), creatorId)) {
            return new ResponseEntity<>("You cannot report your posts!", HttpStatus.BAD_REQUEST);
        }

        ReportPosts newReport = new ReportPosts();
        newReport.setReporter(reporter);
        newReport.setReportedPost(reportedPost);
        newReport.setReason(newReportPostRequest.getReason());
        newReport.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(reportPostsRepository.save(newReport));
    }
}
