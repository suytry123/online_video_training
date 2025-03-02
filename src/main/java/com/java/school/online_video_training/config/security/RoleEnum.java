package com.java.school.online_video_training.config.security;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.java.school.online_video_training.config.security.PermissionEnum.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RoleEnum {
	ADMIN(Set.of(CATEGORY_WRITE, CATEGORY_READ, COURSE_WRITE, COURSE_READ)),
	AUTHOR(Set.of(CATEGORY_READ, COURSE_READ));
	
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
