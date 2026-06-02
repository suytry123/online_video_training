package com.java.school.online_video_training.service;

import com.java.school.online_video_training.entity.User;

public interface UserSecurityService {
    void increaseFailedAttempts(String email);
    void resetFailedAttempts(String email);
    boolean unlockWhenTimeExpired(User user);
}