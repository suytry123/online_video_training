package com.java.school.online_video_training.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.dto.PaymentRequest;
import com.java.school.online_video_training.service.EnrollmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PreAuthorize("hasAuthority('course:write')")
    @PostMapping("/{enrollmentId}/approve")
    public ResponseEntity<?> approve(@PathVariable Long enrollmentId) {
        enrollmentService.approve(enrollmentId);
        return ResponseEntity.ok("Enrollment approved.");
    }

    @PreAuthorize("hasAuthority('course:write')")
    @PostMapping("/{enrollmentId}/reject")
    public ResponseEntity<?> reject(@PathVariable Long enrollmentId) {
        enrollmentService.reject(enrollmentId);
        return ResponseEntity.ok("Enrollment rejected.");
    }
    
    @PreAuthorize("hasAuthority('course:read')")
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payForEnrollment(
            @PathVariable Long id,
            @RequestBody PaymentRequest request) {
        System.out.println("DEBUG: Received PaymentRequest: " + request);
        if (request == null || request.getPrice() == null) {
            System.out.println("DEBUG: PaymentRequest is null or price is null");
            return ResponseEntity.badRequest().body("Missing or null price in request body.");
        }
        if (!enrollmentService.isApproved(id)) {
            return ResponseEntity.status(403).body("Enrollment not approved. Please wait for approval before paying.");
        }
        if (enrollmentService.isPaid(id)) {
            return ResponseEntity.status(403).body("Enrollment already paid.");
        }
        enrollmentService.payForEnrollment(id, request);
        return ResponseEntity.ok("Payment successful. Access granted.");
    }
    
    /*
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payForEnrollment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        // Defensive: check for missing or null price key
        if (body == null || !body.containsKey("price") || body.get("price") == null) {
            return ResponseEntity.badRequest().body("Missing or null price in request body.");
        }
        BigDecimal price;
        try {
            price = new BigDecimal(body.get("price").toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid price in request body.");
        }

        // Check if enrollment is approved and not already paid before allowing payment
        if (!enrollmentService.isApproved(id)) {
            return ResponseEntity.status(403).body("Enrollment not approved. Please wait for approval before paying.");
        }
        if (enrollmentService.isPaid(id)) {
            return ResponseEntity.status(403).body("Enrollment already paid.");
        }

        // Mark as paid and set the amount
        enrollmentService.payForEnrollment(id, price);
        return ResponseEntity.ok("Payment successful. Access granted.");
    }*/
  
    
    /*
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payForEnrollment(
            @PathVariable Long id,
            @RequestBody BigDecimal price) {
    	
    	 if (price == null) {
             return ResponseEntity.badRequest().body("Missing or null price in request body.");
         }
        // Check if enrollment is approved and not already paid before allowing payment
        if (!enrollmentService.isApproved(id)) {
            return ResponseEntity.status(403).body("Enrollment not approved. Please wait for approval before paying.");
        }
        if (enrollmentService.isPaid(id)) {
            return ResponseEntity.status(403).body("Enrollment already paid.");
        }

        // Mark as paid and set the amount
        enrollmentService.payForEnrollment(id, price);
        return ResponseEntity.ok("Payment successful. Access granted.");
    }*/
}
