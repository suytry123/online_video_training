package com.java.school.online_video_training.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.java.school.online_video_training.dto.PageDTO;
import com.java.school.online_video_training.dto.VideoDTO;
import com.java.school.online_video_training.entity.Video;
import com.java.school.online_video_training.mapper.VideoMapper;
import com.java.school.online_video_training.service.VideoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/videos")
public class VideoController {
	private final VideoService videoService;
	private final VideoMapper videoMapper;
	
	@PostMapping
	//@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> createVideo(@RequestBody VideoDTO videoDTO){
		Video video = videoMapper.toVideo(videoDTO);
		video = videoService.createVideo(video);
		return ResponseEntity.ok(videoMapper.toVideoDTO(video));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getVideoById(@PathVariable Long id){
		Video byId = videoService.getVideoById(id);
		return ResponseEntity.ok(videoMapper.toVideoDTO(byId));
	}
	
	@GetMapping
	public ResponseEntity<?> getCourses(@RequestParam Map<String, String> video){
		Page<Video> video1 = videoService.getVideos(video);
		
		PageDTO dto = new PageDTO(video1) ;
		
		return ResponseEntity.ok(dto);
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> updateVideo(@PathVariable Long id, @RequestBody VideoDTO videoDTO){
		Video video = videoMapper.toVideo(videoDTO);
		video = videoService.updateVideo(id, video);
		return ResponseEntity.ok(video);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteVideo(@PathVariable Long id){
		videoService.deleteVideo(id);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/upload")
	public ResponseEntity<?> uploadPicture(@RequestParam("file") MultipartFile file) throws Exception{
		if (file.isEmpty()) {
			throw new RuntimeException("Please load a file");
        }
	
		try {
			videoService.saveImage(file);
			log.info("your image save successful.");
		}
		catch(Exception e){
			e.printStackTrace();
			log.error("Save image Error", e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("path/{path}")
	public ResponseEntity<?> getByPath(@PathVariable String path) throws Exception{
//	    Path filePath = Paths.get(System.getProperty("user.home"), "Pictures", path);
//		 Path filePath = Paths.get(System.getProperty("src"), "main", "resources", "file-repository", path);
		//Path filePath = Paths.get(absolutePath, path);
		 if (path == null || path.isEmpty()) {
		        throw new IllegalArgumentException("File name cannot be null or empty.");
		    }
		 
		 Path filePath = Paths.get("src", "main", "resources", "file-repository", path);
		
	    if (!Files.exists(filePath)) {
	        return ResponseEntity.notFound().build();
	    }

	   byte[] fileBytes = Files.readAllBytes(filePath);
	    //byte[] byPath = videoService.getByPath(path);

	    String contentType = Files.probeContentType(filePath);
	    
	    return ResponseEntity.ok()
	            .contentType(MediaType.parseMediaType(contentType))
	            .body(fileBytes);
	}
	
	
	 @PutMapping("update/{path}")
	    public ResponseEntity<?> updateVideoImage(@PathVariable String path, @RequestParam("file") MultipartFile file) {
	        try {
	            // Call the service method to update the video image (path)
	            videoService.updateImage(path, file);
	            return ResponseEntity.ok().build();
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update video image.");
	        }
	    }
	/*
	 @GetMapping("/images")
		public ResponseEntity<?> getImages(@RequestParam Map<String, String> image) throws Exception{
		 Path path = Paths.get(System.getProperty("user.home") + File.separator + "Pictures" + File.separator);
		 //Path path = Paths.get(System.getProperty("user.home") + File.separator);
		log.info("Directory Path: " + path);
		 if (Files.exists(path)) {
		     log.info("Directory exists!");
		 } else {
		     log.error("Directory does not exist or cannot be accessed.");
		 }
		 try {
			Page<byte[]> images = videoService.getImages(image);
		     byte[] imageBytes = Files.readAllBytes(path);
		     String base64Image = Base64.getEncoder().encodeToString(imageBytes);
		     PageDTO dto = new PageDTO(images) ;
		     Files.readAllBytes(path);
			log.info("your images get successful.");
			 return ResponseEntity.ok(dto);
		 }catch(Exception e) {
			 e.printStackTrace();
				log.error("get images Error", e.getMessage());
		 }
		     
		     // Return the images as a response
		     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update video image.");
		}
		*/
	 
	 @GetMapping("/images")
	 public ResponseEntity<?> getImages(@RequestParam Map<String, String> image) {
		 //Path directoryPath = Paths.get(System.getProperty("user.home"), "Pictures");
	    // Path directoryPath = Paths.get(System.getProperty("user.home") + File.separator + "Pictures" + File.separator);
		// Path directoryPath = Paths.get(System.getProperty("src"), "main", "resources", "file-repository");
		 Path directoryPath = Paths.get("src", "main", "resources", "file-repository");	
		 log.info("Directory Path: {}", directoryPath);
	     
	     if (!Files.exists(directoryPath)) {
	         log.error("Directory does not exist: {}", directoryPath);
	         return ResponseEntity.status(HttpStatus.NOT_FOUND)
	             .body("Images directory not found");
	     }
	     
	     if (!Files.isReadable(directoryPath)) {
	         log.error("Directory is not readable: {}", directoryPath);
	         return ResponseEntity.status(HttpStatus.FORBIDDEN)
	             .body("Cannot access images directory");
	     }
	     
	     try {
	         Page<byte[]> images = videoService.getImages(image);
	         byte[] imageBytes = Files.readAllBytes(directoryPath);
		     String base64Image = Base64.getEncoder().encodeToString(imageBytes);
	         PageDTO dto = new PageDTO(images);
	         log.info("Images retrieved successfully");
	         return ResponseEntity.ok(dto);
	     } catch (Exception e) {
	         log.error("Failed to get images", e);
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	             .body("Failed to retrieve images: " + e.getMessage());
	     }
	 }

	 
	 /*
	 
	 @GetMapping("/images")
	 public ResponseEntity<?> getImages(@RequestParam Map<String, String> image) throws Exception {
//	     Path path = Paths.get(System.getProperty("user.home"), "Pictures");
		 Path path = Paths.get(System.getProperty("user.home") + File.separator + "Pictures" + File.separator);
		 Page<byte[]> images = videoService.getImages(image);
	     byte[] imageBytes = Files.readAllBytes(path);
	     String base64Image = Base64.getEncoder().encodeToString(imageBytes);
	     return ResponseEntity.ok(images);
	 }
	 */
	 
//	 @DeleteMapping("/delete/{path}")
//		public ResponseEntity<?> deleteByPath(@PathVariable String url) throws Exception{
//		 try {
//	            // Call the service method to delete the file
//	            videoService.deleteImageByPath(url);
//	            log.info("Delete successfully: " + url);
//	            return ResponseEntity.ok("Image deleted successfully.");
//	        } catch (Exception e) {
//	            // Log the error and send the failure response
//	            log.error("Delete failed: " + e.getMessage());
//	            return ResponseEntity.status(500).body("Error deleting image: " + e.getMessage());
//	        }
//	 }
	
	 @DeleteMapping("/delete/{url}") 
	 public ResponseEntity<?> deleteByPath(@PathVariable String url) throws Exception {
	     try {
	         // Call the service method to delete the file
	         videoService.deleteImageByPath(url);
	         log.info("Delete successfully: " + url);
	         return ResponseEntity.ok("Image deleted successfully.");
	     } catch (Exception e) {
	         // Log the error and send the failure response
	         log.error("Delete failed: " + e.getMessage());
	         return ResponseEntity.status(500).body("Error deleting image: " + e.getMessage());
	     }
	 }

}
