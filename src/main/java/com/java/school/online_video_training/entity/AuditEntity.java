package com.java.school.online_video_training.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AuditEntity {
	@CreatedDate
	@Column(name = "date_created")
	private LocalDateTime dateCreated;
	
	@LastModifiedDate
	@Column(name = "date_modified")
	private LocalDateTime dateModified;
	
	@CreatedBy
	@Column(name = "user_created")
	private String userCreated;
	
	@LastModifiedBy
	@Column(name = "user_modified")
	private String userModified;
}
