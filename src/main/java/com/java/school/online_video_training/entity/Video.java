package com.java.school.online_video_training.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
@Table(name = "videos")
public class Video extends AuditEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "video_id")
	private Long id;
	
	@Column(name = "video_title")
	private String title;
	
	@Column(name = "video_description")
	private String description;
	
	@ElementCollection
	@Column(name = "video_link")
	private List<String> videoLink;
	
	@Column(name = "image_cover")
	private String imageCover;
	
	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;
	
}

