package com.java.school.online_video_training.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.java.school.online_video_training.dto.VideoDTO;
import com.java.school.online_video_training.dto.VideoResponseDTO;
import com.java.school.online_video_training.entity.Course;
import com.java.school.online_video_training.entity.Enrollment;
import com.java.school.online_video_training.entity.Video;
import com.java.school.online_video_training.exception.FileDeletionException;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.mapper.VideoMapper;
import com.java.school.online_video_training.repository.CourseRepository;
import com.java.school.online_video_training.repository.EnrollmentRepository;
import com.java.school.online_video_training.repository.VideoRepository;
import com.java.school.online_video_training.service.VideoService;
import com.java.school.online_video_training.service.util.PageUtil;
import com.java.school.online_video_training.spec.ImageFilter;
import com.java.school.online_video_training.spec.ImageSpec;
import com.java.school.online_video_training.spec.LinkFilter;
import com.java.school.online_video_training.spec.LinkSpec;
import com.java.school.online_video_training.spec.VideoFilter;
import com.java.school.online_video_training.spec.VideoSpec;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class VideoServiceImpl implements VideoService {
	private final VideoRepository videoRepository;
	private final EnrollmentRepository enrollmentRepository;
	private final CourseRepository courseRepository;
	private final VideoMapper videoMapper;

	@Override
	public VideoResponseDTO createVideo(VideoDTO videoDTO) {
		
		 if (videoDTO.getCourseId() == null) {
		        throw new RuntimeException("CourseId must not be null");
		  }
	    
	    Course course = courseRepository.findById(videoDTO.getCourseId())
	            .orElseThrow(() -> new RuntimeException("Course not found"));
	    
	    Video video = videoMapper.toVideo(videoDTO);
	    
	    video.setCourse(course);
	    
	    Video saved = videoRepository.save(video);
	    return videoMapper.toVideoResponseDTO(saved);
	}

	@Override
	public VideoResponseDTO getVideoById(Long id) {
	    Video video = videoRepository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("Video", id));
	    return videoMapper.toVideoResponseDTO(video);
	}
	
	@Override
	public List<VideoResponseDTO> getVideosByCourse(Long courseId) {

	    if (!courseRepository.existsById(courseId)) {
	        throw new RuntimeException("Course not found");
	    }

	    List<Video> videos = videoRepository.findByCourseId(courseId);

	    return videos.stream()
	            .map(videoMapper::toVideoResponseDTO)
	            .collect(Collectors.toList());
	}

	@Override
	public Page<VideoResponseDTO> getVideos(Map<String, String> video) {
		VideoFilter videoFilter = new VideoFilter();

		if (video.containsKey("title")) {
			String name = video.get("title");
			videoFilter.setTitle(name);
		}

		if (video.containsKey("courseId")) {
			String id = video.get("courseId");
			videoFilter.setTitle(id);
		}

		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
		if (video.containsKey(PageUtil.PAGE_LIMIT)) {
			pageLimit = Integer.parseInt(video.get(PageUtil.PAGE_LIMIT));
		}

		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
		if (video.containsKey(PageUtil.PAGE_NUMBER)) {
			pageNumber = Integer.parseInt(video.get(PageUtil.PAGE_NUMBER));
		}

		VideoSpec videoSpec = new VideoSpec(videoFilter);

		Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);

		Page<Video> page = videoRepository.findAll(videoSpec, pageable);
		return page.map(videoMapper::toVideoResponseDTO);
	}

	@Override
	public VideoResponseDTO updateVideo(Long id, VideoDTO videoDTO) {
	    Video video = videoRepository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("Video", id));
	    Video updateEntity = videoMapper.toVideo(videoDTO);
	    video.setTitle(updateEntity.getTitle());
	  
	    Video updated = videoRepository.save(video);
	    return videoMapper.toVideoResponseDTO(updated);
	}

	@Override
	public void deleteVideo(Long id) {
	    Video video = videoRepository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("Video", id));
	    videoRepository.delete(video);
	    log.info("Video with id = {} is deleted", id);
	}

	/*
	 * @Override public void saveImage(Long id, MultipartFile file) throws Exception
	 * { String folder = Paths.get("src", "main", "resources",
	 * "file-repository").toString(); Files.createDirectories(Paths.get(folder));
	 * 
	 * // Validate if the file already exists Path path = Paths.get(folder,
	 * file.getOriginalFilename()); if (Files.exists(path)) { throw new
	 * Exception("File already exists."); }
	 * 
	 * String relativePath = file.getOriginalFilename();
	 * 
	 * // Fetch the existing video entity Video video =
	 * videoRepository.findById(id).orElseThrow(() -> new
	 * ResourceNotFoundException("Video", id));
	 * 
	 * // Save the file byte[] bytes = file.getBytes(); Files.write(path, bytes);
	 * 
	 * // Update the video entity with the image path
	 * video.setImageCover(relativePath); videoRepository.save(video); // Save the
	 * updated entity }
	 */

	@Override
	public void saveImage(Long id, MultipartFile file) throws Exception {
		Video video = videoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Video", id));
		// Only allow upload if no image_cover exists
		if (video.getImageCover() != null && !video.getImageCover().isEmpty()) {
			throw new IllegalStateException("Image cover already exists. Use PUT to update.");
		}
		String folder = Paths.get("src", "main", "resources", "file-repository").toString();
		Files.createDirectories(Paths.get(folder));
		String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
				? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'))
				: "";
		String fileName = java.util.UUID.randomUUID() + ext;
		Path path = Paths.get(folder, fileName);
		Files.write(path, file.getBytes());
		video.setImageCover(fileName);
		videoRepository.save(video);
	}

	public byte[] getImageCoverById(Long id) throws Exception {
		Video video = videoRepository.findById(id)
				.orElseThrow(() -> new FileNotFoundException("Video not found for id: " + id));
		String imageCover = video.getImageCover();
		if (imageCover == null || imageCover.isEmpty()) {
			throw new FileNotFoundException("No image cover for video id: " + id);
		}
		Path filePath = Paths.get("src", "main", "resources", "file-repository", imageCover);
		if (!Files.exists(filePath)) {
			throw new FileNotFoundException("File not found: " + filePath);
		}
		return Files.readAllBytes(filePath);
	}

	/*
	 * @Override public byte[] getByPath(String path) throws Exception { // Retrieve
	 * the video entity based on the image cover path (assuming the // database
	 * holds the path) Video video = videoRepository.findByImageCover(path)
	 * .orElseThrow(() -> new FileNotFoundException("Video not found for path: " +
	 * path));
	 * 
	 * // Construct the full file path based on the retrieved path in the video
	 * entity Path filePath = Paths.get("src", "main", "resources",
	 * "file-repository", video.getImageCover());
	 * 
	 * // Check if the file exists if (!Files.exists(filePath)) { throw new
	 * FileNotFoundException("File not found: " + filePath); }
	 * 
	 * // Read and return the file as a byte array return
	 * Files.readAllBytes(filePath); }
	 */

	/*
	 * @Override public void updateImage(String path, MultipartFile file) throws
	 * Exception { // Define the folder where images are stored (on the file system)
	 * String folder = Paths.get("src", "main", "resources",
	 * "file-repository").toString();
	 * 
	 * // Validate file if (file.isEmpty()) { throw new Exception("File is empty");
	 * }
	 * 
	 * // Retrieve the existing video entity from the database using the relative
	 * path // (filename) Video video = videoRepository.findByImageCover(path)
	 * .orElseThrow(() -> new ResourceNotFoundException("Video", path));
	 * 
	 * // Construct the old file path using the provided relative path from the //
	 * database (filename) Path oldFilePath = Paths.get(folder, path); // Using
	 * 'path' as filename
	 * 
	 * // Check if the old file exists and delete it if (Files.exists(oldFilePath))
	 * { Files.delete(oldFilePath); // Deletes the old file if it exists }
	 * 
	 * // Create directories if they don't exist
	 * Files.createDirectories(Paths.get(folder));
	 * 
	 * // Get the bytes from the uploaded file byte[] bytes = file.getBytes();
	 * 
	 * // Generate a new filename or use the original filename (relative) String
	 * newFilename = file.getOriginalFilename(); // Using original filename from the
	 * request Path newFilePath = Paths.get(folder, newFilename); // Full path where
	 * file will be stored
	 * 
	 * // Write the new image to the file system Files.write(newFilePath, bytes);
	 * 
	 * // Update the video entity with the new image path (store only the filename
	 * in // DB) video.setImageCover(newFilename); // Save only the filename
	 * (relative path) in DB
	 * 
	 * // Save the updated video entity to the database videoRepository.save(video);
	 * }
	 */

	/*
	 * @Override public void updateImage(String path, MultipartFile file) throws
	 * Exception { String folder = Paths.get("src", "main", "resources",
	 * "file-repository").toString();
	 * 
	 * if (file.isEmpty()) { throw new Exception("File is empty"); }
	 * 
	 * // Retrieve the existing video entity from the database using the relative
	 * path (filename) Video video = videoRepository.findByImageCover(path)
	 * .orElseThrow(() -> new ResourceNotFoundException("Video", path));
	 * 
	 * // Delete the old file if it exists Path oldFilePath = Paths.get(folder,
	 * path); if (Files.exists(oldFilePath)) { Files.delete(oldFilePath); }
	 * 
	 * Files.createDirectories(Paths.get(folder)); byte[] bytes = file.getBytes();
	 * 
	 * // Generate a new unique filename String newFilename = UUID.randomUUID() +
	 * "_" + file.getOriginalFilename(); Path newFilePath = Paths.get(folder,
	 * newFilename); Files.write(newFilePath, bytes);
	 * 
	 * // Update the video entity with the new unique image path
	 * video.setImageCover(newFilename); videoRepository.save(video); }
	 */

	@Override
	public void updateImage(Long id, MultipartFile file) throws Exception {
		String folder = Paths.get("src", "main", "resources", "file-repository").toString();
		if (file.isEmpty()) {
			throw new Exception("File is empty");
		}
		// Retrieve the video entity by id
		Video video = videoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Video", id));
		// Delete the old file if it exists
		String oldImage = video.getImageCover();
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
		// Update the video entity with the new unique image path
		video.setImageCover(newFilename);
		videoRepository.save(video);
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
		Page<Video> page = videoRepository.findAll(spec, pageable);
		return page.map(video -> {
			Map<String, String> dto = new java.util.HashMap<>();
			dto.put("videoId", String.valueOf(video.getId()));
			dto.put("filename", video.getImageCover());
			dto.put("url", "/videos/images/" + video.getImageCover());
			return dto;
		});
	}

	/*
	 * @Override public Page<String> getImages(Map<String, String> images) {
	 * ImageFilter imageFilter = new ImageFilter();
	 * 
	 * if (images.containsKey("imageCover")) { String name =
	 * images.get("imageCover"); imageFilter.setPath(name); }
	 * 
	 * int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT; if
	 * (images.containsKey(PageUtil.PAGE_LIMIT)) { pageLimit =
	 * Integer.parseInt(images.get(PageUtil.PAGE_LIMIT)); }
	 * 
	 * int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER; if
	 * (images.containsKey(PageUtil.PAGE_NUMBER)) { pageNumber =
	 * Integer.parseInt(images.get(PageUtil.PAGE_NUMBER)); }
	 * 
	 * ImageSpec spec = new ImageSpec(imageFilter);
	 * 
	 * Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);
	 * 
	 * Page<Video> page = videoRepository.findAll(spec, pageable); Page<String>
	 * imagePaths = page.map(video -> video.getImageCover());
	 * 
	 * return imagePaths; }
	 */

	public void deleteImageById(Long id) throws Exception {
		String folder = Paths.get("src", "main", "resources", "file-repository").toString();
		Video video = videoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Video", id));
		String imageCover = video.getImageCover();
		if (imageCover == null || imageCover.isEmpty()) {
			throw new FileNotFoundException("No image cover for video id: " + id);
		}
		Path filePath = Paths.get(folder, imageCover);
		if (!Files.exists(filePath)) {
			log.warn("File not found: " + filePath);
			throw new FileNotFoundException("File not found: " + filePath);
		}
		try {
			Files.delete(filePath);
			video.setImageCover(null);
			videoRepository.save(video);
			log.info("Successfully deleted file: " + filePath);
		} catch (IOException e) {
			log.error("Error deleting file: " + filePath, e);
			throw new FileDeletionException("Error deleting file: " + filePath, e);
		}
	}

	/*
	 * @Override public void deleteImageByPath(String url) throws Exception {
	 * 
	 * String folder = Paths.get("src", "main", "resources",
	 * "file-repository").toString();
	 * 
	 * // Construct the full path to the file based on the provided URL Path
	 * filePath = Paths.get(folder, url);
	 * 
	 * // Check if the file exists before trying to delete it if
	 * (!Files.exists(filePath)) { // Log the message if the file doesn't exist
	 * log.warn("File not found: " + filePath); throw new
	 * FileNotFoundException("File not found: " + filePath); // You can throw a
	 * custom exception if // needed } try { Files.delete(filePath); Video video =
	 * videoRepository.findByImageCover(url) .orElseThrow(() -> new
	 * ResourceNotFoundException("Video", url));
	 * 
	 * // Remove the video from the database videoRepository.delete(video);
	 * log.info("Successfully deleted file: " + filePath); } catch (IOException e) {
	 * log.error("Error deleting file: " + filePath, e); throw new
	 * FileDeletionException("Error deleting file: " + filePath, e); } }
	 */

//	@Override
//	public void videoLink(Long id, List<String> link) {
//		Video video = videoRepository.findById(id)
//			.orElseThrow(() -> new ResourceNotFoundException("Video", id));
//		List<String> videoLink = video.getVideoLink();
//		if (videoLink == null) {
//			videoLink = new ArrayList<>(); // This ensures that you can add the link safely
//		}
//		videoLink.addAll(link);
//		video.setVideoLink(videoLink);
//		videoRepository.save(video);
//	}
	/*
	@Override
	public void videoLink(Long id, List<String> link) {
		Video video = videoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Video", id));
		List<String> list = new ArrayList<>();
		list.addAll(link);
		video.setVideoLink(list);
		videoRepository.save(video);
	}

	@Override
	public String getLink(Long id) {
		Video video = videoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Video", id));
		return video.getVideoLink().toString();
	}

	@Override
	public void updateLink(Long id, List<String> link) {
		Video video = videoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Video", id));
//		List<String> videoLink = video.getVideoLink();
//		if (videoLink == null) {
//			videoLink = new ArrayList<>();
//		}
		// videoLink.addAll(link); // Replace old links with the new one
		video.setVideoLink(new ArrayList<>(link));
		videoRepository.save(video);
	}

	@Override
	public Page<String> getLinks(Map<String, String> link) {
		LinkFilter linkFilter = new LinkFilter();

		if (link.containsKey("videoLink")) {
			String name = link.get("videoLink");
			linkFilter.setLinkVideo(name);
		}

		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
		if (link.containsKey(PageUtil.PAGE_LIMIT)) {
			pageLimit = Integer.parseInt(link.get(PageUtil.PAGE_LIMIT));
		}

		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
		if (link.containsKey(PageUtil.PAGE_NUMBER)) {
			pageNumber = Integer.parseInt(link.get(PageUtil.PAGE_NUMBER));
		}

		LinkSpec linkSpec = new LinkSpec(linkFilter);

		Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);

		Page<Video> page = videoRepository.findAll(linkSpec, pageable);
		Page<String> linkVideo = page.map(link1 -> link1.getVideoLink().toString());
		return linkVideo;
	}

	@Override
	public void deleteLink(Long id) {
		Video video = videoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Video", id));

		List<String> videoLinks = video.getVideoLink();
		if (videoLinks != null && !videoLinks.isEmpty()) {
			videoLinks.clear(); // Clears all the links, or you can modify to remove a specific one
			video.setVideoLink(videoLinks);
			videoRepository.save(video);
		} else {
			throw new ResourceNotFoundException("No video links found for video", id); // Optional: Throw if no links
																						// are present
		}
	}*/
	
	@Override
	public Page<VideoResponseDTO> getVideosForUser(Long courseId, Long userId, Map<String, String> params) {
        // Find enrollment for user and course
        Enrollment enrollment = enrollmentRepository.findAll().stream()
            .filter(e -> e.getCourse().getId().equals(courseId) && e.getUser().getId().equals(userId))
            .findFirst().orElse(null);
        if (enrollment == null || "REJECTED".equalsIgnoreCase(enrollment.getStatus())) {
            return Page.empty();
        }
        // Check payment status if needed
        boolean isPaid = "PAID".equalsIgnoreCase(String.valueOf(enrollment.getPaymentStatus()));

        // Parse pagination params
        int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
        if (params.containsKey(PageUtil.PAGE_LIMIT)) {
            pageLimit = Integer.parseInt(params.get(PageUtil.PAGE_LIMIT));
        }
        int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
        if (params.containsKey(PageUtil.PAGE_NUMBER)) {
            pageNumber = Integer.parseInt(params.get(PageUtil.PAGE_NUMBER));
        }
        Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);

        // Only show videos for this course
        Page<Video> videos = videoRepository.findAll(
            (root, query, cb) -> cb.equal(root.get("course").get("id"), courseId), pageable);

        // If not paid, you can filter for free videos here if you have such a flag
        // For now, just return the page as is
        return videos.map(videoMapper::toVideoResponseDTO);
    }
	
	/*
	@Override
	public Page<VideoDTO> getVideosForUser(Long courseId, Long userId, Map<String, String> params) {
        // 1. Check enrollment status
        // 2. If not enrolled or rejected, return empty page
        // 3. If approved but not paid, return only free/limited videos (e.g., videos with isFree=true or first N videos)
        // 4. If paid, return all videos

        // This is a placeholder implementation. You must adapt it to your actual logic and DB structure.
        // You may need to inject EnrollmentRepository and PaymentRepository (if exists) here.
        // For now, let's assume only APPROVED status and paymentStatus in Enrollment entity.

        // TODO: Inject EnrollmentRepository and PaymentRepository if needed
        // Example: private final EnrollmentRepository enrollmentRepository;
        // Example: private final PaymentRepository paymentRepository;

        // 1. Find enrollment
        Enrollment enrollment = enrollmentRepository.findAll().stream()
            .filter(e -> e.getCourse().getId().equals(courseId) && e.getUser().getId().equals(userId))
            .findFirst().orElse(null);
        if (enrollment == null || "REJECTED".equalsIgnoreCase(enrollment.getStatus())) {
            // Not enrolled or rejected
            return Page.empty();
        }
        // 2. Check payment (for now, just check if status is APPROVED and paymentStatus is PAID)
        boolean isPaid = false;
        // If you have payment info in Enrollment, check it here. Otherwise, check Payment table.
        // Example: isPaid = "PAID".equalsIgnoreCase(enrollment.getPaymentStatus());
        // Or query PaymentRepository for a PAID payment for this user/course

        // 3. Pagination
        int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
        if (params.containsKey(PageUtil.PAGE_LIMIT)) {
            pageLimit = Integer.parseInt(params.get(PageUtil.PAGE_LIMIT));
        }
        int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
        if (params.containsKey(PageUtil.PAGE_NUMBER)) {
            pageNumber = Integer.parseInt(params.get(PageUtil.PAGE_NUMBER));
        }
        Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);

        Page<Video> videos;
        if ("APPROVED".equalsIgnoreCase(enrollment.getStatus())) {
            if (isPaid) {
                // Return all videos for the course
                videos = videoRepository.findAll((root, query, cb) -> cb.equal(root.get("course").get("id"), courseId), pageable);
            } else {
                // Return only free/limited videos (for demo, first 1 video)
                videos = videoRepository.findAll((root, query, cb) -> cb.equal(root.get("course").get("id"), courseId), pageable);
                // You may want to filter by a flag (e.g., isFree) or limit to first N videos
                // This is a placeholder: you should implement your own logic
                videos = videos.map(v -> v); // No-op, replace with actual filter
            }
        } else {
            // Not approved
            return Page.empty();
        }
        // Map to DTO
        return videos.map(videoMapper::toVideoDTO);
    }*/

}
