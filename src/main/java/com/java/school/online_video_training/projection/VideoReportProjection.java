package com.java.school.online_video_training.projection;

import java.time.LocalDateTime;

public interface VideoReportProjection {
	String getTitle();
	String getCourseName();
	String getUserCreated();
	String getUserModified();
	LocalDateTime getDateCreated();
	LocalDateTime getDateModified();
}
