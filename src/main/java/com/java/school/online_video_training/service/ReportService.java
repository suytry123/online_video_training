package com.java.school.online_video_training.service;

import java.time.LocalDateTime;
import java.util.List;

import com.java.school.online_video_training.entity.Course;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.entity.Video;
import com.java.school.online_video_training.projection.UserReportProjection;
import com.java.school.online_video_training.projection.VideoReportProjection;

public interface ReportService {

	List<UserReportProjection> getDailyReport();

	List<UserReportProjection> getWeeklyReport();

	List<UserReportProjection> getMonthlyReport();

	List<UserReportProjection> getYearlyReport();
	
	List<VideoReportProjection> getDetailedVideoReport();

	List<UserReportProjection> getUserReportBetweenDate(LocalDateTime start, LocalDateTime end);
	
}