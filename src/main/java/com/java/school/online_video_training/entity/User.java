package com.java.school.online_video_training.entity;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

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
	
	@Column(name = "user_email")
	private String email;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "gender")
	private String gender;
	
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
	
	// for author field
    private String education;
    private String address;
    private String verificationToken;
    
    @Column(name = "is_author")
    private Boolean  isAuthor = false;
    
    @Column(name = "bio")
    private String bio;
    
    @Column(name = "expertise")
    private String expertise;
    
    @Column(name = "temp_author_bio")
    private String tempAuthorBio;
    
    @Column(name = "temp_expertise")
    private String tempExpertise;
    
    @Column(name = "temp_gender")
    private String tempGender;
    
    @Column(name = "temp_phone_number")
    private String tempPhoneNumber;
    
    @Column(name = "temp_education")
    private String tempEducation;
    
    @Column(name = "temp_address")
    private String tempAddress;
    
    @Column(name = "author_approval_requested")
    private Boolean  authorApprovalRequested;
    
    @Column(name = "author_approval_status")
    private String authorApprovalStatus; // PENDING, APPROVED, REJECTED
    
    @Column(name = "author_approved")
    private Boolean  authorApproved = false;
    
    @Column(name = "approve_token")
    private String approveToken;
    
    @Column(name = "reject_token")
    private String rejectToken;
    
    @Column(name = "email_verified")
    private Boolean  emailVerified = false;
	
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