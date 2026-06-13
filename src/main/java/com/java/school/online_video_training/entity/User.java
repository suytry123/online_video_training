package com.java.school.online_video_training.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.java.school.online_video_training.enitity_enum.Gender;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
	
	@Column(name = "user_email", unique = true)
	private String email;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "gender")
    @Enumerated(EnumType.STRING) 
	private Gender gender;
	
	@Column(name = "photo")
	private String photo;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "join_date")
	private LocalDateTime joinDate;
	
	@Column(nullable = false)
	private boolean isDeleted = false;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		    name = "users_roles", 
		    joinColumns = @JoinColumn(name = "user_user_id"),
		    inverseJoinColumns = @JoinColumn(name = "roles_id")
		)
	private Set<Role> roles;
	
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	
	//for lock user
	@Column(name = "failed_attempts", nullable = false)
	private int failedAttempts;

	@Column(name = "lock_time")
	private LocalDateTime lockTime;
	
	//fogot password
	@Column(name = "reset_password_token")
	private String resetPasswordToken;

	@Column(name = "reset_password_expiry")
	private LocalDateTime resetPasswordExpiry;

	//email verification
	@Column(name = "verification_token")
	private String verificationToken;

	@Column(name = "verification_token_expiry")
	private LocalDateTime verificationTokenExpiry;
	
	// for author field
	@OneToMany(mappedBy = "applicant")
	private Set<AuthorApplication> authorApplications = new HashSet<>();
	
	// Constructor for creating new users
	public User(Long id, String username, String email, String password) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.credentialsNonExpired = true;
		this.enabled = true;
	}
	
	@PrePersist
	public void prePersist() {
		if (joinDate == null) {
			joinDate = LocalDateTime.now();
		}
	}
}