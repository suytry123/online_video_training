package com.java.school.online_video_training.service.impl;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
		videoRepository.deleteById(id);
	}
	
	@Override
	public void saveImage(MultipartFile file) throws Exception{
		 String folder = Paths.get("src", "main", "resources", "file-repository").toString();
		    Files.createDirectories(Paths.get(folder));

		    byte[] bytes = file.getBytes();
		    Path path = Paths.get(folder, file.getOriginalFilename());

		    if (Files.exists(path)) {
		        throw new Exception("File already exists.");
		    }

		    Files.write(path, bytes);
	}
	
	public byte[] getByPath(String path) throws Exception {
		 //Path filePath = Paths.get(System.getProperty("user.home"), "Pictures", path);
		//Path filePath = Paths.get(System.getProperty("user.home") + File.separator + "Pictures" + File.separator);
	    //Path filePath = Paths.get(System.getProperty("user.home"), "Pictures", path);
		 Path filePath = Paths.get("src", "main", "resources", "file-repository", path);

		
	    if (!Files.exists(filePath)) {
	        throw new FileNotFoundException("File not found: " + filePath);
	    }

	    return Files.readAllBytes(filePath);
	}
	/*
	@Override
	public void updateImage(String path, MultipartFile file) throws Exception {
		//String folder = System.getProperty("user.home") + File.separator + "Pictures" + File.separator;
		//String folder = Paths.get(System.getProperty("user.home"), "Pictures").toString();
		// Path folder = Paths.get("src", "main", "resources", "file-repository");
		   String folder = Paths.get("src", "main", "resources", "file-repository").toString();
		    // Validate file
		    if (file.isEmpty()) {
		        throw new Exception("File is empty");
		    }
		    
		    Path oldFilePath = Paths.get(folder, path);
		    
		    // Check if the old file exists and delete it
		    if (Files.exists(oldFilePath)) {
		        Files.delete(oldFilePath); // Deletes the old file
		    }


		    //Files.createDirectories(Paths.get(folder));
		    //Files.createDirectories(folder.getParent());
		    Files.createDirectories(Paths.get(folder));
			byte[] bytes = file.getBytes();
			//Path path1 = Paths.get(folder + file.getOriginalFilename());
			String newFilename = file.getOriginalFilename();  // Or use any custom logic here for naming
		    Path newFilePath = Paths.get(folder, newFilename);
		    
		    Files.write(newFilePath , bytes);
	}
	*/
	
	@Override
	public void updateImage(String path, MultipartFile file) throws Exception {
	    // Define the folder where images are stored
	    String folder = Paths.get("src", "main", "resources", "file-repository").toString();
	    
	    // Validate file
	    if (file.isEmpty()) {
	        throw new Exception("File is empty");
	    }
	    
	    // Construct the old file path using the provided path
	    Path oldFilePath = Paths.get(folder, path);
	    
	    // Check if the old file exists and delete it
	    if (Files.exists(oldFilePath)) {
	        Files.delete(oldFilePath); // Deletes the old file if it exists
	    }

	    // Create directories if they don't exist
	    Files.createDirectories(Paths.get(folder));
	    
	    // Get the bytes from the uploaded file
	    byte[] bytes = file.getBytes();
	    
	    // Generate a new filename or use the original filename
	    String newFilename = file.getOriginalFilename();  // Or use any custom logic here for naming
	    Path newFilePath = Paths.get(folder, newFilename);
	    
	    // Write the new image to the file system
	    Files.write(newFilePath, bytes);
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
		
		
		try (Stream<Path> fileStream = Files.list(filePath)) {
	        list = fileStream
	            .filter(Files::isRegularFile)  // Only process files, not directories
	            .filter(p -> p.toString().toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) // Filter for image files
	            .map(p -> {
	                try {
	                    return Files.readAllBytes(p);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    return null; // Return null for errors
	                }
	            })
	            .filter(Objects::nonNull) // Remove failed reads
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
	
	@Override
	public Page<byte[]> getImages(Map<String, String> image) throws Exception {
	    ImageFilter imageFilter = new ImageFilter();
	    List<byte[]> list = new ArrayList<>();

	    // Load the 'file-repository' directory from classpath resources
	    Resource resource = new ClassPathResource("file-repository");

	    // Check if the 'file-repository' folder exists in the classpath
	    if (!resource.exists()) {
	        throw new FileNotFoundException("The 'file-repository' folder was not found in the resources.");
	    }

	    // Convert the resource to a Path object
	    Path filePath = resource.getFile().toPath();

	    // Check if the folder exists
	    if (!Files.exists(filePath)) {
	        throw new FileNotFoundException("The file-repository directory does not exist at the expected location.");
	    }

	    // Apply filters for image search (if any)
	    if (image.containsKey("imageCover")) {
	        String pic = image.get("imageCover");
	        imageFilter.setPath(pic);
	    }

	    int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
	    if (image.containsKey(PageUtil.PAGE_LIMIT)) {
	        pageLimit = Integer.parseInt(image.get(PageUtil.PAGE_LIMIT));
	    }

	    int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
	    if (image.containsKey(PageUtil.PAGE_NUMBER)) {
	        pageNumber = Integer.parseInt(image.get(PageUtil.PAGE_NUMBER));
	    }

	    // Process the files in the file-repository folder
	    try (Stream<Path> fileStream = Files.list(filePath)) {
	        list = fileStream
	                .filter(Files::isRegularFile)  // Only process files, not directories
	                .filter(p -> p.toString().toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) // Filter for image files
	                .map(p -> {
	                    try {
	                        return Files.readAllBytes(p);
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                        return null; // Return null for errors
	                    }
	                })
	                .filter(Objects::nonNull) // Remove failed reads
	                .collect(Collectors.toList());
	    }

	    ImageSpec spec = new ImageSpec(imageFilter);

	    // Create Pageable object for pagination
	    Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);

	    // Paginate the list of images
	    int start = (int) pageable.getOffset();
	    int end = Math.min(start + pageable.getPageSize(), list.size());

	    List<byte[]> pagedImages = list.subList(start, end);

	    // Return paginated images
	    return new PageImpl<>(pagedImages, pageable, list.size());
	}

	
	
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
		
		 //Path filePath = Paths.get(System.getProperty("src"), "main", "resources", "file-repository");
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
		        log.info("Successfully deleted file: " + filePath);
		    } catch (IOException e) {
		        log.error("Error deleting file: " + filePath, e);
		        throw new FileDeletionException("Error deleting file: " + filePath, e);
		    }
	}


	
}
