package com.java.school.online_video_training.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.dto.ReportDashboardResponse;
import com.java.school.online_video_training.projection.UserReportProjection;
import com.java.school.online_video_training.projection.VideoReportProjection;
import com.java.school.online_video_training.service.ReportService;
import com.java.school.online_video_training.service.util.JasperReportUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/report")
public class ReportController {

	private final ReportService reportService;
	private final JasperReportUtil jasperReportUtil;

	@PreAuthorize("hasAuthority('report:read')")
    @GetMapping("/daily")
    public ResponseEntity<byte[]> daily() throws Exception {
        return buildUserReport(
                reportService.getDailyReport(),
                "Daily User Report",
                "daily_report.pdf"
        );
    }

    @PreAuthorize("hasAuthority('report:read')")
    @GetMapping("/weekly")
    public ResponseEntity<byte[]> weekly() throws Exception {
        return buildUserReport(
                reportService.getWeeklyReport(),
                "Weekly User Report",
                "weekly_report.pdf"
        );
    }

    @PreAuthorize("hasAuthority('report:read')")
    @GetMapping("/monthly")
    public ResponseEntity<byte[]> monthly() throws Exception {
        return buildUserReport(
                reportService.getMonthlyReport(),
                "Monthly User Report",
                "monthly_report.pdf"
        );
    }

    @PreAuthorize("hasAuthority('report:read')")
    @GetMapping("/yearly")
    public ResponseEntity<byte[]> yearly() throws Exception {
        return buildUserReport(
                reportService.getYearlyReport(),
                "Yearly User Report",
                "yearly_report.pdf"
        );
    }

    @PreAuthorize("hasAuthority('report:read')")
    @GetMapping("/between")
    public ResponseEntity<byte[]> between(
            @RequestParam String start,
            @RequestParam String end) throws Exception {

        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);

        return buildUserReport(
                reportService.getUserReportBetweenDate(startDate, endDate),
                "Custom Report (" + start + " → " + end + ")",
                "custom_report.pdf"
        );
    }

    @PreAuthorize("hasAuthority('report:read')")
    @GetMapping("/video")
    public ResponseEntity<byte[]> video() throws Exception {

        List<VideoReportProjection> data = reportService.getDetailedVideoReport();

        Map<String, Object> params = new HashMap<>();
        params.put("REPORT_TITLE", "Video Report");

        byte[] pdf = jasperReportUtil.generateReport(data, "video_report", params);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=video_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private ResponseEntity<byte[]> buildUserReport(
            List<UserReportProjection> data,
            String title,
            String filename) throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("REPORT_TITLE", title);

        byte[] pdf = jasperReportUtil.generateReport(data, "user_report", params);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    
    @GetMapping("/dashboard")
	public ResponseEntity<ReportDashboardResponse> dashboard(
			@RequestParam(defaultValue = "weekly") String period,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
		return ResponseEntity.ok(reportService.getReportDashboard(period, start, end));
	}

	/*
	 * @GetMapping("/daily") public ResponseEntity<List<UserReportProjection>>
	 * dailyReport() { return ResponseEntity.ok(reportService.getDailyReport()); }
	 * 
	 * @GetMapping("/weekly") public ResponseEntity<List<UserReportProjection>>
	 * weeklyReport() { return ResponseEntity.ok(reportService.getWeeklyReport()); }
	 * 
	 * @GetMapping("/monthly") public ResponseEntity<List<UserReportProjection>>
	 * monthlyReport() { return ResponseEntity.ok(reportService.getMonthlyReport());
	 * }
	 * 
	 * @GetMapping("/yearly") public ResponseEntity<List<UserReportProjection>>
	 * yearlyReport() { return ResponseEntity.ok(reportService.getYearlyReport()); }
	 * 
	 * @GetMapping("/dropdown") public ResponseEntity<List<VideoReportProjection>>
	 * getVideoDetailedVideoReport() { return
	 * ResponseEntity.ok(reportService.getDetailedVideoReport()); }
	 * 
	 * @GetMapping("/specific/{startDate}/{endDate}") public
	 * ResponseEntity<List<UserReportProjection>> getSpecificReport(
	 * 
	 * @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @PathVariable("startDate")
	 * LocalDateTime start,
	 * 
	 * @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @PathVariable("endDate")
	 * LocalDateTime end) { if (start.isAfter(end)) { return
	 * ResponseEntity.badRequest().body(Collections.emptyList()); } return
	 * ResponseEntity.ok(reportService.getUserReportBetweenDate(start, end)); }
	 */
}
