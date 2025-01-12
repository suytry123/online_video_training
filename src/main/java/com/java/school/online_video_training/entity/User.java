package com.java.school.online_video_training.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;
	
	@Column(name = "user_name")
	private String username;
	
	@Column(name = "user_password")
	private String password;
	
	@Column(name = "user_email")
	private String email;
	
	@Column(name = "phone_number")
	private Integer phoneNumber;
	
	@Column(name = "gender")
	private String gender;
	
	@Column(name = "photo")
	private String photo;
	
	@Column(name = "join_date")
	private LocalDateTime joinDate;

}

