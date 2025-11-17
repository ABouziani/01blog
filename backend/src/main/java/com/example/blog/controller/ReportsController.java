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

import com.example.blog.dto.NewReportPostRequest;
import com.example.blog.dto.NewReportRequest;
import com.example.blog.model.ReportPosts;
import com.example.blog.model.ReportUsers;
import com.example.blog.service.ReportPostsService;
import com.example.blog.service.ReportUsersService;
import com.example.blog.service.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    private ReportUsersService reportUsersService;

    @Autowired
    private ReportPostsService reportPostsService;

    @PostMapping("/adduserreport")
    public ResponseEntity<ReportUsers> addReportUser(@Valid @RequestBody NewReportRequest reportReq) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long loggedUserId = userDetails.getId();
        ReportUsers savedReport = reportUsersService.addReport(reportReq, loggedUserId);
        return ResponseEntity.ok(savedReport);
    }

    @PostMapping("/addpostreport")
    public ResponseEntity<ReportPosts> addReportPost(@Valid @RequestBody NewReportPostRequest postReportReq) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long loggedUserId = userDetails.getId();
        ReportPosts savedReport = reportPostsService.addReport(postReportReq, loggedUserId);
        return ResponseEntity.ok(savedReport);
    }

    @GetMapping("/getalluserreport")
    public List<ReportUsers> getReports() {
        return reportUsersService.getAllReports();
    }

}
