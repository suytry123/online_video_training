package com.java.school.online_video_training.config.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PermissionEnum {
	CATEGORY_WRITE("category:write"),
	CATEGORY_READ("category:read"),
	COURSE_WRITE("course:write"),
	COURSE_READ("course:read"),
	VIDEO_WRITE("video:write"),
	VIDEO_READ("video:read"),
	USER_WRITE("user:write"),
	USER_READ("user:read"),
	LOGO_UPDATE("logo:update");
	
	
	private String description;
	
	
}
