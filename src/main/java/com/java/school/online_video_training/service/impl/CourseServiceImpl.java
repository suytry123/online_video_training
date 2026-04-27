package com.java.school.online_video_training.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.dto.CourseDTO;
import com.java.school.online_video_training.dto.CourseDetailDTO;
import com.java.school.online_video_training.dto.CourseResponseDTO;
import com.java.school.online_video_training.dto.CourseSummaryDTO;
import com.java.school.online_video_training.dto.VideoDTO;
import com.java.school.online_video_training.entity.Category;
import com.java.school.online_video_training.entity.Course;
import com.java.school.online_video_training.entity.Enrollment;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.mapper.CourseMapper;
import com.java.school.online_video_training.repository.CategoryRepository;
import com.java.school.online_video_training.repository.CourseRepository;
import com.java.school.online_video_training.repository.EnrollmentRepository;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.repository.VideoRepository;
import com.java.school.online_video_training.service.CourseService;
import com.java.school.online_video_training.service.util.PageUtil;
import com.java.school.online_video_training.spec.CourseFilter;
import com.java.school.online_video_training.spec.CourseSpec;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CourseServiceImpl implements CourseService {

	private final CourseRepository courseRepository;
	private final CategoryRepository categoryRepository;
	private final VideoRepository videoRepository;
	private final UserRepository userRepository;
	private final EnrollmentRepository enrollmentRepository;
	private final CourseMapper courseMapper;

	// private final CourseMapper courseMapper;

//	@Override
//	public Course create(CourseDTO courseDTO) {
//		Course course = courseMapper.toCourse(courseDTO);
//		return courseRepository.save(course);
//	}


    @Override
    public CourseResponseDTO create(CourseDTO courseDTO) {
    	
    	 if (courseDTO.getCategoryId() == null) {
    	     throw new RuntimeException("Category ID is required");
    	 }

    	 if (courseDTO.getAuthorId() == null) {
    	     throw new RuntimeException("Author ID is required");
    	 }
        
        Category category = categoryRepository.findById(courseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        User author = userRepository.findById(courseDTO.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));
        
        Course course = courseMapper.toCourse(courseDTO);
        
        course.setCategory(category);
        course.setAuthor(author);
        
        Course saved = courseRepository.save(course);
        
        return courseMapper.toCourseDTO(saved);
    }

    @Override
    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course", id));
        return courseMapper.toCourseDTO(course);
    }

	@Override
	public Page<CourseResponseDTO> getCourses(Map<String, String> course) {
		CourseFilter courseFilter = new CourseFilter();

		if (course.containsKey("name")) {
			String name = course.get("name");
			courseFilter.setName(name);
		}

		if (course.containsKey("id")) {
			String id = course.get("id");
			courseFilter.setCategoryId(Long.parseLong(id));
		}

		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
		if (course.containsKey(PageUtil.PAGE_LIMIT)) {
			pageLimit = Integer.parseInt(course.get(PageUtil.PAGE_LIMIT));
		}

		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
		if (course.containsKey(PageUtil.PAGE_NUMBER)) {
			pageNumber = Integer.parseInt(course.get(PageUtil.PAGE_NUMBER));
		}

		CourseSpec courseSpec = new CourseSpec(courseFilter);

		Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);

		Page<Course> page = courseRepository.findAll(courseSpec, pageable);
		return page.map(courseMapper::toCourseDTO);
	}

	  @Override
	    public CourseResponseDTO update(Long id, CourseDTO courseUpdate) {
	        Course course = courseRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Course", id));
	        Course updateEntity = courseMapper.toCourse(courseUpdate);
	        course.setName(updateEntity.getName());
	        course.setCategory(updateEntity.getCategory());
	        course.setAuthor(updateEntity.getAuthor());
	        Course updated = courseRepository.save(course);
	        return courseMapper.toCourseDTO(updated);
	    }

	@Override
	public void delete(Long id) {
		courseRepository.deleteById(id);
	}

	@Override
	public List<CourseSummaryDTO> getAllCourses() {
	    return courseRepository.findAll().stream().map(course -> {
	        CourseSummaryDTO dto = new CourseSummaryDTO();
	        dto.setId(course.getId());
	        dto.setName(course.getName());
	        dto.setCategoryId(course.getCategory() != null ? course.getCategory().getId() : null);
	        dto.setAuthorName(course.getAuthor() != null ? course.getAuthor().getUsername() : null);
	        dto.setViews(course.getViews());
	        dto.setLikes(course.getLikes());
	        return dto;
	    }).collect(Collectors.toList());
	}

	@Override
	public CourseDetailDTO getCourseDetail(Long courseId) {
	    Course course = courseRepository.findById(courseId)
	        .orElseThrow(() -> new RuntimeException("Course not found"));
	    CourseDetailDTO dto = new CourseDetailDTO();
	    dto.setId(course.getId());
	    dto.setName(course.getName());
	    dto.setCategoryId(course.getCategory() != null ? course.getCategory().getId() : null);
	    dto.setAuthorName(course.getAuthor() != null ? course.getAuthor().getUsername() : null);
	    dto.setViews(course.getViews());
	    dto.setLikes(course.getLikes());
	    List<VideoDTO> videos = course.getVideos().stream().map(video -> {
	        VideoDTO vdto = new VideoDTO();
	        vdto.setId(video.getId());
	        vdto.setCourseId(course.getId());
	        vdto.setTitle(video.getTitle());
	        vdto.setDescription(video.getDescription());
	        // vdto.setVideoUrl(video.getVideoUrl()); // Only if you have this field
	        return vdto;
	    }).collect(Collectors.toList());
	    dto.setVideos(videos);
	    return dto;
	}
	
	@Override
	public void enroll(Long courseId, Long userId) {
        // 1. Fetch course and user
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Prevent duplicate enrollments (simple check)
        boolean alreadyEnrolled = enrollmentRepository.findAll().stream()
        		 .anyMatch(e -> e.getCourse().getId().equals(courseId)
                         && e.getUser().getId().equals(userId)
                         && (e.getStatus() == null || !"CANCELLED".equalsIgnoreCase(e.getStatus()))); // Null-safe check
        if (alreadyEnrolled) {
            throw new RuntimeException("User is already enrolled or has a pending enrollment for this course.");
        }

        // 3. Create and save enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setUser(user);
        enrollment.setStatus("PENDING");
        enrollment.setPaymentStatus("UNPAID");
        enrollment.setPrice(BigDecimal.ZERO); // Set to 0.0 to satisfy NOT NULL constraint
        enrollmentRepository.save(enrollment);
    }
	
/*
	@Override
	public void enroll(Long courseId, Long userId) {
	    Course course = courseRepository.findById(courseId)
	        .orElseThrow(() -> new RuntimeException("Course not found"));
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new RuntimeException("User not found"));

	    Enrollment enrollment = new Enrollment();
	    enrollment.setCourse(course);
	    enrollment.setUser(user);
	    enrollment.setStatus("PENDING");
	    enrollment.setPaymentStatus("UNPAID");
	    enrollmentRepository.save(enrollment);
	}*/
	
}
