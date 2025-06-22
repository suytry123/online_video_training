//package com.java.school.online_video_training.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.java.school.online_video_training.config.security.UserService;
//import com.java.school.online_video_training.entity.User;
//import com.java.school.online_video_training.service.EmailService;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@RestController
//@RequestMapping("/admin")
//public class AdminController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private EmailService emailService;
//
//    @PostMapping("/approve-author/{userId}")
//    public ResponseEntity<String> approveAuthor(@PathVariable Long userId) {
//        try {
//            User user = userService.approveAuthor(userId);
//            emailService.sendAuthorApprovalStatusEmail(user, true);
//            return ResponseEntity.ok("Author approved successfully");
//        } catch (Exception e) {
//            log.error("Error approving author: {}", e.getMessage());
//            return ResponseEntity.badRequest().body("Error approving author: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/reject-author/{userId}")
//    public ResponseEntity<String> rejectAuthor(@PathVariable Long userId) {
//        try {
//            User user = userService.rejectAuthor(userId);
//            emailService.sendAuthorApprovalStatusEmail(user, false);
//            return ResponseEntity.ok("Author rejected successfully");
//        } catch (Exception e) {
//            log.error("Error rejecting author: {}", e.getMessage());
//            return ResponseEntity.badRequest().body("Error rejecting author: " + e.getMessage());
//        }
//    }
//}
