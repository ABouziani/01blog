package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @Override
    Optional<Report> findById(Long id);
    Optional<List<Report>> findByReporterId(Long reporterId);
    Optional<List<Report>> findByReportedId(Long reportedId);
    @Override
    List<Report> findAll();
    boolean existsByReporterIdAndReportedId(Long reporterId, Long reportedId);
}
