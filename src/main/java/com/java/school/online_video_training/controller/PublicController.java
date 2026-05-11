package com.java.school.online_video_training.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.dto.StatisticsDTO;
import com.java.school.online_video_training.service.PublicService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public")
public class PublicController {
	
	private final PublicService publicService;

	@GetMapping("/statistics")
	public ResponseEntity<StatisticsDTO>
	statistics() {

	    return ResponseEntity.ok(
	            publicService.getStatistics()
	    );

	}
	
}
