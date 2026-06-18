package com.java.school.online_video_training.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.java.school.online_video_training.enitity_enum.CourseType;

import lombok.Data;

@Data
@Entity
@Table(name = "courses")
public class Course {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "course_id")
	private Long id;
	
	@Column(name = "course_name")
	private String name;
	 
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
	
	@ManyToOne
	@JoinColumn(name = "author_id")
	private User author;

	@Column(name = "views")
	private Integer views = 0;

	@Column(name = "likes")
	private Integer likes = 0;
	
	@Column(name = "image_cover")
	private String imageCover;
	
	@Column(name = "course_description")
	private String courseDescription;
	
	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal price = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CourseType courseType = CourseType.FREE;
	
	@Column(nullable = false)
	private boolean isDeleted = false;

	@OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
	private List<Video> videos;
}