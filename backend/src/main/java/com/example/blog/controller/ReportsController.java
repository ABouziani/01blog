package com.example.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.NewReportRequest;
import com.example.blog.model.Report;
import com.example.blog.service.ReportService;
import com.example.blog.service.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/add")
    public ResponseEntity<Report> addReport(@Valid @RequestBody NewReportRequest reportReq) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long loggedUserId = userDetails.getId();
        Report savedReport = reportService.addReport(reportReq,loggedUserId);
        return ResponseEntity.ok(savedReport); 
    }

    @GetMapping("/getall")
    public List<Report> getReports() {
        return reportService.getAllReports();
    }

    
}
