package com.java.school.online_video_training.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportSummaryDTO {
	private final long totalUsers;
	private final long usersInRange;
	private final long totalVideos;
}