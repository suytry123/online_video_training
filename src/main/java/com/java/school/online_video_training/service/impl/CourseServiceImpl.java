package com.java.school.online_video_training.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.java.school.online_video_training.dto.CourseDTO;
import com.java.school.online_video_training.dto.CourseDetailDTO;
import com.java.school.online_video_training.dto.CourseResponseDTO;
import com.java.school.online_video_training.dto.CourseSummaryDTO;
import com.java.school.online_video_training.dto.VideoDTO;
import com.java.school.online_video_training.entity.Category;
import com.java.school.online_video_training.entity.Course;
import com.java.school.online_video_training.entity.CourseLike;
import com.java.school.online_video_training.entity.CourseView;
import com.java.school.online_video_training.entity.Enrollment;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.exception.FileDeletionException;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.mapper.CourseMapper;
import com.java.school.online_video_training.repository.CategoryRepository;
import com.java.school.online_video_training.repository.CourseLikeRepository;
import com.java.school.online_video_training.repository.CourseRepository;
import com.java.school.online_video_training.repository.CourseViewRepository;
import com.java.school.online_video_training.repository.EnrollmentRepository;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.repository.VideoRepository;
import com.java.school.online_video_training.service.CourseService;
import com.java.school.online_video_training.service.util.PageUtil;
import com.java.school.online_video_training.spec.CourseFilter;
import com.java.school.online_video_training.spec.CourseSpec;
import com.java.school.online_video_training.spec.ImageFilter;
import com.java.school.online_video_training.spec.ImageSpec;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

	private final CourseRepository courseRepository;
	private final CategoryRepository categoryRepository;
	private final VideoRepository videoRepository;
	private final UserRepository userRepository;
	private final CourseLikeRepository courseLikeRepository;
	private final CourseViewRepository courseViewRepository;
	private final EnrollmentRepository enrollmentRepository;
	private final CourseMapper courseMapper;

//	@Override
//	public Course create(CourseDTO courseDTO) {
//		Course course = courseMapper.toCourse(courseDTO);
//		return courseRepository.save(course);
//	}

	@Override
	@Transactional
	public CourseResponseDTO create(CourseDTO courseDTO) {

		if (courseDTO.getCategoryId() == null) {
			throw new RuntimeException("Category ID is required");
		}

		if (courseDTO.getAuthorId() == null) {
			throw new RuntimeException("Author ID is required");
		}

		Category category = categoryRepository.findById(courseDTO.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Category not found"));

		User author = userRepository.findUserById(courseDTO.getAuthorId())
				.filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("AUTHOR")))
				.orElseThrow(() -> new RuntimeException("Author not found or not AUTHOR"));

		Course course = courseMapper.toCourse(courseDTO);

		course.setCategory(category);
		course.setAuthor(author);

		Course saved = courseRepository.save(course);

		return courseMapper.toCourseDTO(saved);
	}

	@Override
	public CourseResponseDTO getCourseById(Long id) {
		Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));
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
		Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));
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
			dto.setImageCover(course.getImageCover());
			dto.setViews(course.getViews());
			dto.setLikes(course.getLikes());
			dto.setPrice(course.getPrice());
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public CourseDetailDTO getCourseDetail(Long courseId) {

		Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

		CourseDetailDTO dto = new CourseDetailDTO();

		dto.setId(course.getId());
		dto.setName(course.getName());

		dto.setCategoryName(course.getCategory() != null ? course.getCategory().getName() : null);

		dto.setAuthorName(course.getAuthor() != null ? course.getAuthor().getUsername() : null);

		dto.setViews(course.getViews());

		dto.setLikes(course.getLikes());

		List<VideoDTO> videos = course.getVideos().stream().map(video -> {

			VideoDTO vdto = new VideoDTO();

			vdto.setId(video.getId());

			vdto.setCourseId(course.getId());

			vdto.setTitle(video.getTitle());

			vdto.setDescription(video.getDescription());

			vdto.setVideoLink(video.getVideoLink());
			// optional
			// vdto.setVideoUrl(video.getVideoUrl());

			return vdto;
		}).collect(Collectors.toList());

		dto.setVideos(videos);

		return dto;
	}

	public void addView(Long courseId, Long userId) {

		boolean exists = courseViewRepository.existsByUserIdAndCourseId(userId, courseId);

		if (exists) {
			return;
		}

		Course course = courseRepository.findById(courseId).orElseThrow();

		User user = userRepository.findById(userId).orElseThrow();

		CourseView view = new CourseView();

		view.setCourse(course);
		view.setUser(user);

		courseViewRepository.save(view);

		course.setViews(course.getViews() + 1);

		courseRepository.save(course);
	}

	public void likeCoruse(Long courseId, Long userId) {

		boolean exists = courseLikeRepository.existsByUserIdAndCourseId(userId, courseId);

		if (exists) {
			return;
		}

		Course course = courseRepository.findById(courseId).orElseThrow();

		User user = userRepository.findById(userId).orElseThrow();

		CourseLike like = new CourseLike();

		like.setCourse(course);
		like.setUser(user);

		courseLikeRepository.save(like);

		course.setLikes(course.getLikes() + 1);

		courseRepository.save(course);
	}

	public void unlikeCourse(Long courseId, Long userId) {

		CourseLike like = courseLikeRepository.findByUserIdAndCourseId(userId, courseId).orElseThrow();

		courseLikeRepository.delete(like);

		Course course = courseRepository.findById(courseId).orElseThrow();

		course.setLikes(course.getLikes() - 1);

		courseRepository.save(course);
	}

	/*
	 * @Override public CourseDetailDTO getCourseDetail(Long courseId) { Course
	 * course = courseRepository.findById(courseId) .orElseThrow(() -> new
	 * RuntimeException("Course not found")); CourseDetailDTO dto = new
	 * CourseDetailDTO(); dto.setId(course.getId()); dto.setName(course.getName());
	 * // dto.setCategoryId(course.getCategory() != null ?
	 * course.getCategory().getId() : null); dto.setAuthorName(course.getAuthor() !=
	 * null ? course.getAuthor().getUsername() : null);
	 * dto.setViews(course.getViews()); dto.setLikes(course.getLikes());
	 * List<VideoDTO> videos = course.getVideos().stream().map(video -> { VideoDTO
	 * vdto = new VideoDTO(); // vdto.setId(video.getId());
	 * vdto.setCourseId(course.getId()); vdto.setTitle(video.getTitle());
	 * vdto.setDescription(video.getDescription()); //
	 * vdto.setVideoUrl(video.getVideoUrl()); // Only if you have this field return
	 * vdto; }).collect(Collectors.toList()); dto.setVideos(videos); return dto; }
	 */

	@Override
	public void enroll(Long courseId, Long userId) {
		// 1. Fetch course and user
		Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		// 2. Prevent duplicate enrollments (simple check)
		boolean alreadyEnrolled = enrollmentRepository.findAll().stream()
				.anyMatch(e -> e.getCourse().getId().equals(courseId) && e.getUser().getId().equals(userId)
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

	@Override
	public void saveImage(Long id, MultipartFile file) throws Exception {

		Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));

		// Only allow upload if no image_cover exists
		if (course.getImageCover() != null && !course.getImageCover().isEmpty()) {

			throw new IllegalStateException("Image cover already exists. Use PUT to update.");
		}

		String contentType = file.getContentType();

		if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png")
				&& !contentType.equals("image/webp"))) {

			throw new RuntimeException("Only image files allowed");
		}
	
		String folder = Paths.get("uploads", "courses").toString();

		Files.createDirectories(Paths.get(folder));

		String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
				? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'))
				: "";

		String fileName = UUID.randomUUID() + ext;

		Path path = Paths.get(folder, fileName);

		Files.write(path, file.getBytes());

		course.setImageCover(fileName);

		courseRepository.save(course);
	}

	@Override
	public byte[] getImageCoverById(Long id) throws Exception {

		Course course = courseRepository.findById(id)
				.orElseThrow(() -> new FileNotFoundException("Course not found for id: " + id));

		String imageCover = course.getImageCover();

		if (imageCover == null || imageCover.isEmpty()) {

			throw new FileNotFoundException("No image cover for course id: " + id);
		}

		Path filePath = Paths.get("uploads", "courses", imageCover);

		if (!Files.exists(filePath)) {

			throw new FileNotFoundException("File not found: " + filePath);
		}

		return Files.readAllBytes(filePath);
	}

	@Override
	public void updateImage(Long id, MultipartFile file) throws Exception {

		String folder = Paths.get("uploads", "courses").toString();

		if (file.isEmpty()) {

			throw new Exception("File is empty");
		}

		String contentType = file.getContentType();

		if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png")
				&& !contentType.equals("image/webp"))) {

			throw new RuntimeException("Only image files allowed");
		}

		// Retrieve the course entity by id
		Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));

		// Delete the old file if it exists
		String oldImage = course.getImageCover();

		if (oldImage != null && !oldImage.isEmpty()) {

			Path oldFilePath = Paths.get(folder, oldImage);

			Files.deleteIfExists(oldFilePath);
		}

		Files.createDirectories(Paths.get(folder));

		String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
				? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'))
				: "";

		String newFilename = UUID.randomUUID() + ext;

		Path newFilePath = Paths.get(folder, newFilename);

		Files.write(newFilePath, file.getBytes());

		// Update course entity with new image
		course.setImageCover(newFilename);

		courseRepository.save(course);
	}

	@Override
	public Page<Map<String, String>> getImages(Map<String, String> images) {

		ImageFilter imageFilter = new ImageFilter();

		if (images.containsKey("imageCover")) {

			String name = images.get("imageCover");

			imageFilter.setPath(name);
		}

		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;

		if (images.containsKey(PageUtil.PAGE_LIMIT)) {

			pageLimit = Integer.parseInt(images.get(PageUtil.PAGE_LIMIT));
		}

		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;

		if (images.containsKey(PageUtil.PAGE_NUMBER)) {

			pageNumber = Integer.parseInt(images.get(PageUtil.PAGE_NUMBER));
		}

		ImageSpec spec = new ImageSpec(imageFilter);

		Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);

		Page<Course> page = courseRepository.findAll(spec, pageable);

		return page.map(course -> {

			Map<String, String> dto = new HashMap<>();

			dto.put("courseId", String.valueOf(course.getId()));

			dto.put("filename", course.getImageCover());

			dto.put("url", "/courses/images/" + course.getId());

			return dto;
		});
	}

	@Override
	public void deleteImageById(Long id) throws Exception {

		String folder = Paths.get("uploads", "courses").toString();

		Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));

		String imageCover = course.getImageCover();

		if (imageCover == null || imageCover.isEmpty()) {

			throw new FileNotFoundException("No image cover for course id: " + id);
		}

		Path filePath = Paths.get(folder, imageCover);

		if (!Files.exists(filePath)) {

			log.warn("File not found: " + filePath);

			throw new FileNotFoundException("File not found: " + filePath);
		}

		try {

			Files.delete(filePath);

			course.setImageCover(null);

			courseRepository.save(course);

			log.info("Successfully deleted file: " + filePath);

		} catch (IOException e) {

			log.error("Error deleting file: " + filePath, e);

			throw new FileDeletionException("Error deleting file: " + filePath, e);
		}
	}

	/*
	 * @Override public void enroll(Long courseId, Long userId) { Course course =
	 * courseRepository.findById(courseId) .orElseThrow(() -> new
	 * RuntimeException("Course not found")); User user =
	 * userRepository.findById(userId) .orElseThrow(() -> new
	 * RuntimeException("User not found"));
	 * 
	 * Enrollment enrollment = new Enrollment(); enrollment.setCourse(course);
	 * enrollment.setUser(user); enrollment.setStatus("PENDING");
	 * enrollment.setPaymentStatus("UNPAID"); enrollmentRepository.save(enrollment);
	 * }
	 */

}
