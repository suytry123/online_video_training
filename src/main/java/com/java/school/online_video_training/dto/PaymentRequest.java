package com.java.school.online_video_training.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PaymentRequest {
	private final BigDecimal price;
}
