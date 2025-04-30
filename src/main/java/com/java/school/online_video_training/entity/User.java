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
	
	@Column(name = "join_date")
	private LocalDateTime joinDate;
	
	
	
	@ManyToMany(fetch = FetchType.EAGER)
//	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
//	inverseJoinColumns = @JoinColumn(name = "role_id"))
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
    private boolean isAuthor = false;
    
    @Column(name = "bio")
    private String bio;
    
    @Column(name = "expertise")
    private String expertise;
    
    @Column(name = "author_approval_requested")
    private boolean authorApprovalRequested = false;
    
    @Column(name = "author_approval_status")
    private String authorApprovalStatus; // PENDING, APPROVED, REJECTED
    
    @Column(name = "author_approved")
    private boolean authorApproved = false;
    
    @Column(name = "approve_token")
    private String approveToken;
    
    @Column(name = "reject_token")
    private String rejectToken;
    
    @Column(name = "email_verified")
    private boolean emailVerified = false;
	
	public User(String username, String email, String password) {
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
