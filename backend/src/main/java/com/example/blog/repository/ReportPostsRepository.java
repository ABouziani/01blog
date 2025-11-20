package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.model.Post;
import com.example.blog.model.ReportPosts;

public interface ReportPostsRepository extends JpaRepository<ReportPosts, Long> {
    @Override
    Optional<ReportPosts> findById(Long id);
    Optional<List<ReportPosts>> findByReporterId(Long reporterId);
    Optional<List<ReportPosts>> findByReportedpostId(Long reportedpostId);
    @Override
    List<ReportPosts> findAll();

    void deleteByReportedpost(Post reportedPost);

    boolean existsByReporterIdAndReportedpostId(Long reporterId, Long reportedpostId);
}
