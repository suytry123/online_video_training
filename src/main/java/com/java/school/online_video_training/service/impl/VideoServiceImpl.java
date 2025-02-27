package com.java.school.online_video_training.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.java.school.online_video_training.entity.Video;
import com.java.school.online_video_training.exception.FileDeletionException;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.repository.VideoRepository;
import com.java.school.online_video_training.service.VideoService;
import com.java.school.online_video_training.service.util.PageUtil;
import com.java.school.online_video_training.spec.ImageFilter;
import com.java.school.online_video_training.spec.ImageSpec;
import com.java.school.online_video_training.spec.VideoFilter;
import com.java.school.online_video_training.spec.VideoSpec;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class VideoServiceImpl implements VideoService{
	private final VideoRepository videoRepository;
	
	@Override
	public Video createVideo(Video video) {
		return videoRepository.save(video);
	}

	@Override
	public Video getVideoById(Long id) {
		return videoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Video", id));
	}

	@Override
	public Page<Video> getVideos(Map<String, String> video) {
		VideoFilter videoFilter = new VideoFilter();
		
		if(video.containsKey("title")) {
			String name = video.get("title");
			videoFilter.setTitle(name);
		}
		
		if(video.containsKey("courseId")) {
			String id = video.get("courseId");
			videoFilter.setTitle(id);
		}
		
		
		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
		if(video.containsKey(PageUtil.PAGE_LIMIT)) {
			pageLimit = Integer.parseInt(video.get(PageUtil.PAGE_LIMIT));
		}
		
		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
		if(video.containsKey(PageUtil.PAGE_NUMBER)){
			pageNumber = Integer.parseInt(video.get(PageUtil.PAGE_NUMBER));
		}
		
		VideoSpec videoSpec = new VideoSpec(videoFilter);
		
		 Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);
		
		 Page<Video> page = videoRepository.findAll(videoSpec, pageable);
		 return page;
	}

	@Override
	public Video updateVideo(Long id, Video videoUpdate) {
		Video video = getVideoById(id);
		video.setTitle(videoUpdate.getTitle());
		return videoRepository.save(video);
	}

	@Override
	public void deleteVideo(Long id) {
		Video videoById = getVideoById(id);
		videoRepository.delete(videoById);
		log.info("video with id = %ld is deleted".formatted(id));
	}
	
	@Override
	public void saveImage(Long id, MultipartFile file) throws Exception {
	    String folder = Paths.get("src", "main", "resources", "file-repository").toString();
	    Files.createDirectories(Paths.get(folder));

	    // Validate if the file already exists
	    Path path = Paths.get(folder, file.getOriginalFilename());
	    if (Files.exists(path)) {
	        throw new Exception("File already exists.");
	    }
	    
	    String relativePath = file.getOriginalFilename(); 
	    
	    // Fetch the existing video entity
	    Video video = videoRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Video", id));

	    // Save the file
	    byte[] bytes = file.getBytes();
	    Files.write(path, bytes);

	    // Update the video entity with the image path
	    video.setImageCover(relativePath);
	    videoRepository.save(video); // Save the updated entity
	}

	
	@Override
	public byte[] getByPath(String path) throws Exception {
	    // Retrieve the video entity based on the image cover path (assuming the database holds the path)
	    Video video = videoRepository.findByImageCover(path)
	            .orElseThrow(() -> new FileNotFoundException("Video not found for path: " + path));

	    // Construct the full file path based on the retrieved path in the video entity
	    Path filePath = Paths.get("src", "main", "resources", "file-repository", video.getImageCover());

	    // Check if the file exists
	    if (!Files.exists(filePath)) {
	        throw new FileNotFoundException("File not found: " + filePath);
	    }

	    // Read and return the file as a byte array
	    return Files.readAllBytes(filePath);
	}

	
	@Override
	public void updateImage(String path, MultipartFile file) throws Exception {
	    // Define the folder where images are stored (on the file system)
	    String folder = Paths.get("src", "main", "resources", "file-repository").toString();
	    
	    // Validate file
	    if (file.isEmpty()) {
	        throw new Exception("File is empty");
	    }

	    // Retrieve the existing video entity from the database using the relative path (filename)
	    Video video = videoRepository.findByImageCover(path)
	            .orElseThrow(() -> new ResourceNotFoundException("Video", path));

	    // Construct the old file path using the provided relative path from the database (filename)
	    Path oldFilePath = Paths.get(folder, path);  // Using 'path' as filename

	    // Check if the old file exists and delete it
	    if (Files.exists(oldFilePath)) {
	        Files.delete(oldFilePath);  // Deletes the old file if it exists
	    }

	    // Create directories if they don't exist
	    Files.createDirectories(Paths.get(folder));

	    // Get the bytes from the uploaded file
	    byte[] bytes = file.getBytes();
	    
	    // Generate a new filename or use the original filename (relative)
	    String newFilename = file.getOriginalFilename();  // Using original filename from the request
	    Path newFilePath = Paths.get(folder, newFilename);  // Full path where file will be stored
	    
	    // Write the new image to the file system
	    Files.write(newFilePath, bytes);
	    
	    // Update the video entity with the new image path (store only the filename in DB)
	    video.setImageCover(newFilename);  // Save only the filename (relative path) in DB
	    
	    // Save the updated video entity to the database
	    videoRepository.save(video);
	}


	
	@Override
	public Page<String> getImages(Map<String, String> images) {
		ImageFilter imageFilter = new ImageFilter(); 
		
		if(images.containsKey("imageCover")) {
			String name = images.get("imageCover");
			
		}
		
		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
		if(images.containsKey(PageUtil.PAGE_LIMIT)) {
			pageLimit = Integer.parseInt(images.get(PageUtil.PAGE_LIMIT));
		}
		
		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
		if(images.containsKey(PageUtil.PAGE_NUMBER)){
			pageNumber = Integer.parseInt(images.get(PageUtil.PAGE_NUMBER));
		}
		
		ImageSpec spec = new ImageSpec(imageFilter);
		
		 Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);	 
		
		 Page<Video> page = videoRepository.findAll(spec, pageable);
		 Page<String> imagePaths = page.map(video -> video.getImageCover());
		 
		 return imagePaths;
	}


	
	/*@Override
	public Page<byte[]> getImages(Map<String, String> image) throws Exception{
		ImageFilter  imageFilter = new ImageFilter();
		List<byte[]> list = new ArrayList<>();
//		String folder = System.getProperty("user.home") + File.separator + "Pictures" + File.separator;
		//Path filePath = Paths.get(System.getProperty("user.home") + File.separator + "Pictures" + File.separator);
		 //Path filePath = Paths.get(System.getProperty("user.home"), "Pictures");
		//Path filePath = Paths.get(System.getProperty("user.home") + File.separator);
		 Path filePath = Paths.get(System.getProperty("src"), "main", "resources", "file-repository");
		
		if(image.containsKey("imageCover")) {
			String pic = image.get("imageCover");
			imageFilter.setPath(pic);
		}
		
		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
		if(image.containsKey(PageUtil.PAGE_LIMIT)) {
			pageLimit = Integer.parseInt(image.get(PageUtil.PAGE_LIMIT));
		}
		
		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
		if(image.containsKey(PageUtil.PAGE_NUMBER)){
			pageNumber = Integer.parseInt(image.get(PageUtil.PAGE_NUMBER));
		}
		
		
			    List<byte[]> collect = list.stream()
	    	.filter(p -> p.toString().toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$"))
	    	.map(p -> {
	    		try {
	    			return Files.readAllBytes(directoryPath);
	    		}catch(IOException e) {
	    			 log.error("Error reading file: {}", p, e);
	    			 return null;
	    		}
	    	})
	    	.filter(Objects::nonNull)
	    	.collect(Collectors.toList());
		}
			
		 
		ImageSpec spec = new ImageSpec(imageFilter);
		
		//byte[] allBytes = Files.readAllBytes(filePath);
		
		 Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);
		 
		    int start = (int) pageable.getOffset();
		    int end = Math.min(start + pageable.getPageSize(), list.size());
		    
		    List<byte[]> pagedImages = list.subList(start, end);
		
		 return new PageImpl<>(pagedImages , pageable, list.size());
	}*/
	
	
	
	/*
	@Override
	public Page<byte[]> getImages(Map<String, String> image) throws Exception {
	    ImageFilter imageFilter = new ImageFilter();
	    List<byte[]> list = new ArrayList<>();

	    // Define the image directory path
	    String folder = System.getProperty("user.home") + File.separator + "Pictures" + File.separator;
	    Path filePath = Paths.get(folder);

	    // Debugging: Print the resolved directory path
	    System.out.println("Reading images from directory: " + filePath.toString());

	    // Ensure directory exists
	    if (!Files.exists(filePath) || !Files.isDirectory(filePath)) {
	        throw new FileNotFoundException("Directory not found: " + filePath.toString());
	    }

	    // Set filter if provided
	    if (image.containsKey("imageCover")) {
	        imageFilter.setPath(image.get("imageCover"));
	    }

	    // Get pagination values
	    int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
	    if (image.containsKey(PageUtil.PAGE_LIMIT)) {
	        pageLimit = Integer.parseInt(image.get(PageUtil.PAGE_LIMIT));
	    }

	    int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
	    if (image.containsKey(PageUtil.PAGE_NUMBER)) {
	        pageNumber = Integer.parseInt(image.get(PageUtil.PAGE_NUMBER));
	    }

	    // Read images from the directory
	    try (Stream<Path> fileStream = Files.list(filePath)) {
	        list = fileStream
	            .filter(Files::isRegularFile)  // Ensure it's a file, not a directory
	            .filter(p -> {
	                try {
	                    String contentType = Files.probeContentType(p);
	                    return contentType != null && contentType.startsWith("image");
	                } catch (IOException e) {
	                    System.err.println("Failed to determine content type: " + p.toString());
	                    return false;
	                }
	            })
	            .map(p -> {
	                try {
	                    return Files.readAllBytes(p);
	                } catch (IOException e) {
	                    System.err.println("Failed to read file: " + p.toString());
	                    return null;
	                }
	            })
	            .filter(Objects::nonNull) // Remove failed reads
	            .collect(Collectors.toList());
	    }

	    // Pagination
	    Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);
	    int start = (int) pageable.getOffset();
	    int end = Math.min(start + pageable.getPageSize(), list.size());
	    List<byte[]> pagedImages = list.subList(start, end);

	    return new PageImpl<>(pagedImages, pageable, list.size());
	}
	*/

	@Override
	public void deleteImageByPath(String url) throws Exception{
		
		String folder = Paths.get("src", "main", "resources", "file-repository").toString();
	    
	    // Construct the full path to the file based on the provided URL
	    Path filePath = Paths.get(folder, url); 

		    // Check if the file exists before trying to delete it
		    if (!Files.exists(filePath)) {
		        // Log the message if the file doesn't exist
		        log.warn("File not found: " + filePath);
		        throw new FileNotFoundException("File not found: " + filePath); // You can throw a custom exception if needed
		    }
		    try {
		        Files.delete(filePath);
		        Video video = videoRepository.findByImageCover(url)
		                .orElseThrow(() -> new ResourceNotFoundException("Video", url));
		        
		        // Remove the video from the database
		        videoRepository.delete(video);
		        log.info("Successfully deleted file: " + filePath);
		    } catch (IOException e) {
		        log.error("Error deleting file: " + filePath, e);
		        throw new FileDeletionException("Error deleting file: " + filePath, e);
		    }
	}

	
}
