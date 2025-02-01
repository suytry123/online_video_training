package com.java.school.online_video_training.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.java.school.online_video_training.entity.Video;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.repository.VideoRepository;
import com.java.school.online_video_training.service.VideoService;
import com.java.school.online_video_training.service.util.PageUtil;
import com.java.school.online_video_training.spec.VideoFilter;
import com.java.school.online_video_training.spec.VideoSpec;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VideoServiceImpl implements VideoService{
	private final VideoRepository videoRepository;

	@Override
	public void saveImage(MultipartFile file) throws Exception{
		String folder = System.getProperty("user.home") + File.separator + "Pictures" + File.separator;
//		String folder = "/Pictures/";
		Files.createDirectories(Paths.get(folder));
		byte[] bytes = file.getBytes();
		Path path = Paths.get(folder + file.getOriginalFilename());
	    Files.write(path, bytes);
	}
	
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
		
		if(video.containsKey("videoTitle")) {
			String name = video.get("videoTitle");
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

//	@Override
//	public Byte[] getByPath(String path) throws Exception{
////		 Path filePath = Paths.get(path);
////		 byte[] allBytes = Files.readAllBytes(filePath);
////		 return videoRepository.findByImageCover(allBytes.toString());
//		String folder = System.getProperty("user.home") + File.separator + "Pictures" + File.separator;
//		Path filePath = Paths.get(folder, path);
//		byte[] allBytes = Files.readAllBytes(filePath);
//		return videoRepository.findByImageCover(allBytes);
//	}
	
	public byte[] getByPath(String path) throws Exception {
//	    Path filePath = Paths.get(System.getProperty("user.home"), "Pictures", path);
//	    if (!Files.exists(filePath)) {
//	        throw new FileNotFoundException("File not found: " + filePath);
//	    }
//	    byte[] allBytes = Files.readAllBytes(filePath);
//	    return videoRepository.findByImageCover(new String(allBytes));
		 // Construct full file path
	    Path filePath = Paths.get(System.getProperty("user.home"), "Pictures", path);

	    // Check if file exists
	    if (!Files.exists(filePath)) {
	        throw new FileNotFoundException("File not found: " + filePath);
	    }

	    // Read and return the image file as byte[]
	    return Files.readAllBytes(filePath);
	}

}
