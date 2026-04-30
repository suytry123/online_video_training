package com.java.school.online_video_training.entity;

import java.time.LocalDateTime;

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
	private LocalDateTime dateCreated;
	
	@LastModifiedDate
	private LocalDateTime dateModified;
	
	@CreatedBy
    private String userCreated;
	
	@LastModifiedBy
	private String userModified;
}