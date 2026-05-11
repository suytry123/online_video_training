package com.java.school.online_video_training.service.impl;

import org.springframework.stereotype.Service;

import com.java.school.online_video_training.dto.StatisticsDTO;
import com.java.school.online_video_training.repository.CourseRepository;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.service.PublicService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PublicServcieImpl implements PublicService{
	
	private final UserRepository userRepository;
	private final CourseRepository courseRepository;
	
	@Override
	public StatisticsDTO getStatistics() {

	    Long totalStudents =
	            userRepository.count();

	    Long totalCourses =
	            courseRepository.count();

	    Long totalInstructors =
	            userRepository.countByRoleName(
	                    "ROLE_AUTHOR"
	            );

	    return new StatisticsDTO(
	            totalStudents,
	            totalCourses,
	            totalInstructors
	    );

	}
}
