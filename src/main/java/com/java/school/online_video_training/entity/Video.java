package com.java.school.online_video_training.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table
public class Video {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "video_id")
	private Long id;
	
	@Column(name = "video_title")
	private String title;
	
	@Column(name = "video_link")
	private String videoLink;
	
	@Column(name = "image_cover")
	private String imageCover;
	
	@Column(name = "date_create")
	private LocalDateTime dateCreate;
	
	@Column(name = "date_modified")
	private LocalDateTime dateModified;
	
	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;
	
	@ManyToOne
	@JoinColumn(name = "user_created")
	private User userCreated;
	
	@ManyToOne
	@JoinColumn(name = "user_modified")
	private User userModified;
}

