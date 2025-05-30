package com.java.school.online_video_training.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
@Entity
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String status; // e.g., "PENDING", "APPROVED", "REJECTED"
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price; // Amount paid for this enrollment (null or 0 if unpaid)

    @Column(nullable = false)
    private String paymentStatus; // e.g., "UNPAID", "PAID"

    // (Optional) Add timestamps for auditing
     @CreationTimestamp
     private LocalDateTime createdAt;
     
     @UpdateTimestamp
     private LocalDateTime updatedAt;
    
//    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;
    

}
