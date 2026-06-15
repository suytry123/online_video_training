package com.java.school.online_video_training.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.java.school.online_video_training.config.security.AuthorApprovalStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "author_applications",
    indexes = {
        @Index(name = "idx_author_application_user", columnList = "user_id"),
        @Index(name = "idx_author_application_status", columnList = "status")
    }
)
public class AuthorApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User applicant;

    @Column(nullable = false)
    private String education;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 2000)
    private String bio;

    @Column(nullable = false, length = 1000)
    private String expertise;
    

    @Column(name = "cv_file_name", nullable = false, length = 255)
    private String cvFileName;

    @Column(name = "cv_file_path", nullable = false, length = 500)
    private String cvFilePath;

//    @Column(name = "action_token", unique = true, nullable = false, length = 512)
//    private String actionToken;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rejected_by")
    private User rejectedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthorApprovalStatus status;
}