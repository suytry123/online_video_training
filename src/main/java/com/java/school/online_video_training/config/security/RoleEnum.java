package com.java.school.online_video_training.config.security;

import static com.java.school.online_video_training.config.security.PermissionEnum.CATEGORY_READ;
import static com.java.school.online_video_training.config.security.PermissionEnum.CATEGORY_WRITE;
import static com.java.school.online_video_training.config.security.PermissionEnum.COURSE_READ;
import static com.java.school.online_video_training.config.security.PermissionEnum.COURSE_WRITE;
import static com.java.school.online_video_training.config.security.PermissionEnum.LOGO_UPDATE;
import static com.java.school.online_video_training.config.security.PermissionEnum.USER_READ;
import static com.java.school.online_video_training.config.security.PermissionEnum.USER_WRITE;
import static com.java.school.online_video_training.config.security.PermissionEnum.VIDEO_READ;
import static com.java.school.online_video_training.config.security.PermissionEnum.VIDEO_WRITE;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RoleEnum {
	ADMIN(Set.of(CATEGORY_WRITE, CATEGORY_READ, COURSE_WRITE, COURSE_READ, VIDEO_WRITE
			, VIDEO_READ, USER_WRITE, USER_READ)),
	AUTHOR(Set.of(CATEGORY_WRITE, CATEGORY_READ, COURSE_WRITE, COURSE_READ, VIDEO_WRITE
			,VIDEO_READ, USER_WRITE, USER_READ)),
	USER(Set.of(USER_READ, USER_WRITE, COURSE_READ, VIDEO_READ));
	
	private Set<PermissionEnum> permissions;
	
	public Set<SimpleGrantedAuthority> getAuthorities(){
		Set<SimpleGrantedAuthority> grantedAuthorities = this.permissions.stream()
			.map(permission -> new SimpleGrantedAuthority(permission.getDescription()))
			.collect(Collectors.toSet());
			
		SimpleGrantedAuthority roles = new SimpleGrantedAuthority("ROLE_" + this.name());
		grantedAuthorities.add(roles);
		return grantedAuthorities;
	}
}