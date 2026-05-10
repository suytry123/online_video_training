package com.java.school.online_video_training.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportDashboardResponse {
	private final String period;
	private final String start;
	private final String end;
	private final ReportSummaryDTO summary;
	private final List<ChartPointDTO> userTrend;
	private final List<ChartPointDTO> roleBreakdown;
}