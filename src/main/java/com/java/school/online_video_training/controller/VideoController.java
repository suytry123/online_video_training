package com.java.school.online_video_training.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> createVideo(@RequestBody VideoDTO videoDTO){
		Video video = videoMapper.toVideo(videoDTO);
		video = videoService.createVideo(video);
		return ResponseEntity.ok(videoMapper.toVideoDTO(video));
	}
	
	@GetMapping("{id}")
	public ResponseEntity<?> getVideoById(@PathVariable("id") Long id){
		Video byId = videoService.getVideoById(id);
		return ResponseEntity.ok(byId);
	}
	
	@GetMapping
	public ResponseEntity<?> getCourses(@RequestParam("id") Map<String, String> video){
		Page<Video> video1 = videoService.getVideos(video);
		
		PageDTO dto = new PageDTO(video1) ;
		
		return ResponseEntity.ok(dto);
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> updateVideo(@RequestParam("id") Long id, @RequestBody VideoDTO videoDTO){
		Video video = videoMapper.toVideo(videoDTO);
		video = videoService.updateVideo(id, video);
		return ResponseEntity.ok(video);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteVideo(@RequestParam Long id){
		videoService.deleteVideo(id);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/upload")
	public ResponseEntity<?> uploadPicture(@RequestParam("file") MultipartFile file){
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
}
