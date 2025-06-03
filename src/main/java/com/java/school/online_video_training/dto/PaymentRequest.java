package com.java.school.online_video_training.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class PaymentRequest {
	private BigDecimal price;
}
