package com.java.school.online_video_training.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface UserPaidReportProjection {
    String getUsername();
    String getEmail();
    BigDecimal getPaidAmount();
    LocalDateTime getPaidDate();
}
