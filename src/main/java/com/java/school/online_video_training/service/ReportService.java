package com.java.school.online_video_training.service;

import java.time.LocalDateTime;
import java.util.List;

import com.java.school.online_video_training.projection.UserReportProjection;

public interface ReportService {

	List<UserReportProjection> getDailyReport();

	List<UserReportProjection> getWeeklyReport();

	List<UserReportProjection> getMonthlyReport();

	List<UserReportProjection> getYearlyReport();

	List<UserReportProjection> getDropDownReport(LocalDateTime start, LocalDateTime end);

	List<UserReportProjection> getSpecificReport(LocalDateTime start, LocalDateTime end);
}
