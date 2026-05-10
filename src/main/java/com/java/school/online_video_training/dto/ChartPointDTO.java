package com.java.school.online_video_training.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChartPointDTO {
	private final String label;
	private final long value;
}