package com.java.school.online_video_training.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;
	
	@NotEmpty
	@NotNull
	@Column(name = "user_name")
	private String username;
	
	@NotNull
	@NotEmpty
	@Column(name = "user_password")
	private String password;
	
	@NotNull
	@NotEmpty
	@Column(name = "user_email")
	private String email;
	
	@NotEmpty
	@NotNull
	@Column(name = "phone_number")
	private Integer phoneNumber;
	
	@NotEmpty
	@NotNull
	@Column(name = "gender")
	private String gender;
	
	@NotNull
	@NotNull
	@Column(name = "photo")
	private String photo;
	
	@Column(name = "join_date")
	private LocalDateTime joinDate;

}

