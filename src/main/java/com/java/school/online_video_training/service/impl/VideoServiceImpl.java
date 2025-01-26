package com.java.school.online_video_training.service.impl;

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
		String folder = "/Pictures/";
		byte[] bytes = file.getBytes();
		Path path = Paths.get(folder + file.getOriginalFilename());
		Path write = Files.write(path, bytes);
		
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

}
