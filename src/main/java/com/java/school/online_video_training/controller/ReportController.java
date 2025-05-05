package com.java.school.online_video_training.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.projection.UserReportProjection;
import com.java.school.online_video_training.service.ReportService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily")
    public ResponseEntity<List<UserReportProjection>> dailyReport() {
        return ResponseEntity.ok(reportService.getDailyReport());
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<UserReportProjection>> weeklyReport() {
        return ResponseEntity.ok(reportService.getWeeklyReport());
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<UserReportProjection>> monthlyReport() {
        return ResponseEntity.ok(reportService.getMonthlyReport());
    }

    @GetMapping("/yearly")
    public ResponseEntity<List<UserReportProjection>> yearlyReport() {
        return ResponseEntity.ok(reportService.getYearlyReport());
    }

    @GetMapping("/detailed/{startDate}/{endDate}")
    public ResponseEntity<List<UserReportProjection>> dropDownReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @PathVariable("startDate") LocalDateTime start, 
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @PathVariable("endDate") LocalDateTime end) {
        if (start.isAfter(end)) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        return ResponseEntity.ok(reportService.getDropDownReport(start, end));
    }

    @GetMapping("/specific/{startDate}/{endDate}")
    public ResponseEntity<List<UserReportProjection>> specificReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @PathVariable("startDate") LocalDateTime start, 
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @PathVariable("endDate") LocalDateTime end) {
        if (start.isAfter(end)) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        return ResponseEntity.ok(reportService.getSpecificReport(start, end));
    }
}
