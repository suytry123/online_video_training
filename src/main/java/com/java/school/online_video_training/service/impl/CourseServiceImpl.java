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
import org.springframework.http.HttpStatus;
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
import com.java.school.online_video_training.enums.CourseType;
import com.java.school.online_video_training.enums.EnrollmentStatus;
import com.java.school.online_video_training.enums.PaymentStatus;
import com.java.school.online_video_training.exception.ApiException;
import com.java.school.online_video_training.exception.FileDeletionException;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.mapper.CourseMapper;
import com.java.school.online_video_training.repository.CategoryRepository;
import com.java.school.online_video_training.repository.CourseLikeRepository;
import com.java.school.online_video_training.repository.CourseRepository;
import com.java.school.online_video_training.repository.CourseViewRepository;
import com.java.school.online_video_training.repository.EnrollmentRepository;
import com.java.school.online_video_training.repository.UserRepository;
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
	private final UserRepository userRepository;
	private final CourseLikeRepository courseLikeRepository;
	private final CourseViewRepository courseViewRepository;
	private final EnrollmentRepository enrollmentRepository;
	private final CourseMapper courseMapper;
	
	private static final String AUTHOR_ROLE = "AUTHOR";

	@Override
	@Transactional
	public CourseResponseDTO create(CourseDTO courseDTO, String username) {

		if (courseDTO.getCategoryId() == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Category ID is required");
		}

		Category category = categoryRepository.findById(courseDTO.getCategoryId()).filter(c -> !c.isDeleted())
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));

		User author = userRepository.findByUsername(username)
				.filter(user -> !user.isDeleted()
						&& user.getRoles().stream().anyMatch(role -> AUTHOR_ROLE.equals(role.getName())))
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Author not found"));

		if (courseDTO.getCourseType() == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Course type is required");
		}

		Course course = courseMapper.toCourse(courseDTO);

		if (courseDTO.getCourseType() == CourseType.FREE) {
			if (courseDTO.getPrice() != null && courseDTO.getPrice().compareTo(BigDecimal.ZERO) != 0) {

				throw new ApiException(HttpStatus.BAD_REQUEST, "Free course cannot have a price");
			}

			course.setPrice(BigDecimal.ZERO);

		} else {

			if (courseDTO.getPrice() == null || courseDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {

				throw new ApiException(HttpStatus.BAD_REQUEST, "Paid course price must be greater than 0");
			}

			course.setPrice(courseDTO.getPrice());
		}

		course.setCourseType(courseDTO.getCourseType());

		course.setCategory(category);

		course.setAuthor(author);

		Course saved = courseRepository.save(course);

		return courseMapper.toCourseDTO(saved);
	}
	
	@Override
	public CourseResponseDTO getCourseById(Long id) {
		Course course = courseRepository.findById(id).filter(c -> !c.isDeleted())
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

//		Course course = courseRepository.findById(id).filter(c -> !c.isDeleted())
//				.orElseThrow(() -> new ResourceNotFoundException("Course", id));
		Course course = getEntityById(id);

		if (courseUpdate.getName() != null) {
			course.setName(courseUpdate.getName());
		}

		if (courseUpdate.getCourseDescription() != null) {
			course.setCourseDescription(courseUpdate.getCourseDescription());
		}

		if (courseUpdate.getCategoryId() != null) {

			Category category = categoryRepository.findById(courseUpdate.getCategoryId()).filter(c -> !c.isDeleted())
					.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));

			course.setCategory(category);

		}

		CourseType courseType = courseUpdate.getCourseType() != null ? courseUpdate.getCourseType()
				: course.getCourseType();

		BigDecimal price = courseUpdate.getPrice() != null ? courseUpdate.getPrice() : course.getPrice();

		if (courseType == CourseType.FREE) {

			if (price != null && price.compareTo(BigDecimal.ZERO) != 0) {

				throw new ApiException(HttpStatus.BAD_REQUEST, "Free course cannot have a price");
			}

			course.setPrice(BigDecimal.ZERO);

		} else {

			if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {

				throw new ApiException(HttpStatus.BAD_REQUEST, "Paid course price must be greater than 0");
			}

			course.setPrice(price);
		}

		course.setCourseType(courseType);

		Course updated = courseRepository.save(course);

		return courseMapper.toCourseDTO(updated);
	}

	@Override
	public void delete(Long id) {
//		courseRepository.deleteById(id);
		Course course = getEntityById(id);
		course.setDeleted(true);
		courseRepository.save(course);
	}

	private Course getEntityById(Long id) {
		return courseRepository.findById(id).filter(c -> !c.isDeleted())
				.orElseThrow(() -> new ResourceNotFoundException("Course", id));
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
			dto.setCourseType(course.getCourseType());
			dto.setCourseDescription(course.getCourseDescription());
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public CourseDetailDTO getCourseDetail(Long courseId) {

		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));

		CourseDetailDTO dto = new CourseDetailDTO();

		dto.setId(course.getId());
		dto.setName(course.getName());

		dto.setCategoryName(course.getCategory() != null ? course.getCategory().getName() : null);

		dto.setAuthorName(course.getAuthor() != null ? course.getAuthor().getUsername() : null);

		dto.setViews(course.getViews());

		dto.setLikes(course.getLikes());
		dto.setCourseType(course.getCourseType());
		dto.setPrice(course.getPrice());

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

	@Override
	public List<CourseResponseDTO> getTrash() {

		List<Course> courses = courseRepository.findByIsDeletedTrue();

		return courses.stream().map(courseMapper::toCourseDTO).toList();

	}

	@Override
	public void restore(Long id) {

		Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));

		course.setDeleted(false);

		courseRepository.save(course);

	}

	@Override
	@Transactional
	public void enroll(Long courseId, Long userId) {

		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));

		if (enrollmentRepository.existsByCourseIdAndUserId(courseId, userId)) {

			throw new IllegalStateException("You have already enrolled in this course.");
		}

		Enrollment enrollment = new Enrollment();

		enrollment.setCourse(course);
		enrollment.setUser(user);

		enrollment.setStatus(EnrollmentStatus.PENDING);
		enrollment.setPaymentStatus(PaymentStatus.UNPAID);

		enrollment.setPrice(BigDecimal.ZERO);

		enrollmentRepository.save(enrollment);
	}

	@Override
	public void saveImage(Long id, MultipartFile file) throws Exception {

		Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));

		// Only allow upload if no image_cover exists
		if (course.getImageCover() != null && !course.getImageCover().isBlank()) {

			throw new IllegalStateException("Image cover already exists. Use PUT to update.");
		}

		if (file == null || file.isEmpty()) {
			throw new RuntimeException("File is empty");
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

		if (imageCover == null || imageCover.isBlank()) {
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

		if (file == null || file.isEmpty()) {

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

		if (oldImage != null && !oldImage.isBlank()) {

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

		Course course = courseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Course", id));

		String imageCover = course.getImageCover();

		if (imageCover == null || imageCover.isBlank()) {

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
