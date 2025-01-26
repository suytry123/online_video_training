package com.java.school.online_video_training.service.impl;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.entity.Course;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.repository.CourseRepository;
import com.java.school.online_video_training.service.CourseService;
import com.java.school.online_video_training.service.util.PageUtil;
import com.java.school.online_video_training.spec.CourseFilter;
import com.java.school.online_video_training.spec.CourseSpec;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CourseServiceImpl implements CourseService{
	private final CourseRepository courseRepository; 
	
	//private final CourseMapper courseMapper;

//	@Override
//	public Course create(CourseDTO courseDTO) {
//		Course course = courseMapper.toCourse(courseDTO);
//		return courseRepository.save(course);
//	}
	
	@Override
	public Course create(Course course) {
		return courseRepository.save(course);
	}

	@Override
	public Course getById(Long id) {
		return courseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Course", id));
	}

	@Override
	public Page<Course> getCourses(Map<String, String> course) {
		CourseFilter courseFilter = new CourseFilter();
		
		if(course.containsKey("name")) {
			String name = course.get("name");
			courseFilter.setName(name);
		}
		
		if(course.containsKey("id")) {
			String id = course.get("id");
			courseFilter.setCategoryId(Long.parseLong(id));
		}
		
		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
		if(course.containsKey(PageUtil.PAGE_LIMIT)) {
			pageLimit = Integer.parseInt(course.get(PageUtil.PAGE_LIMIT));
		}
		
		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
		if(course.containsKey(PageUtil.PAGE_NUMBER)){
			pageNumber = Integer.parseInt(course.get(PageUtil.PAGE_NUMBER));
		}
		
		CourseSpec courseSpec = new CourseSpec(courseFilter);
		
		 Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);
		
		 Page<Course> page = courseRepository.findAll(courseSpec, pageable);
		 return page;
	}

	@Override
	public Course update(Long id, Course courseUpdate) {
		Course course = getById(id);
		course.setName(courseUpdate.getName());
		return courseRepository.save(course);
	}

	@Override
	public void delete(Long id) {
		courseRepository.deleteById(id);
	}

}
