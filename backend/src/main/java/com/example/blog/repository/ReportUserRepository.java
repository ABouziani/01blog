package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.model.ReportUsers;
import com.example.blog.model.User;

public interface ReportUserRepository extends JpaRepository<ReportUsers, Long> {
    @Override
    Optional<ReportUsers> findById(Long id);
    Optional<List<ReportUsers>> findByReporterId(Long reporterId);
    Optional<List<ReportUsers>> findByReportedId(Long reportedId);
    @Override
    List<ReportUsers> findAll();
    boolean existsByReporterIdAndReportedId(Long reporterId, Long reportedId);

    void deleteByReported(User reported);
}
