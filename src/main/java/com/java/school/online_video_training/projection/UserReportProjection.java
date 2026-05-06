package com.java.school.online_video_training.projection;

import java.time.LocalDateTime;

public interface UserReportProjection {
	Long getId();
	String getUsername();
	String getEmail();
	LocalDateTime getJoinDate();
	LocalDateTime getCreatedAt();
}
