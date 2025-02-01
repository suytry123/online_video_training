package com.java.school.online_video_training.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.java.school.online_video_training.entity.Video;

public interface VideoService {
	Video createVideo(Video video);
	//Video createVideo(MultipartFile file,Video video)throws Exception;
	Video getVideoById(Long id);
	Page<Video> getVideos(Map<String, String> video);
	Video updateVideo(Long id, Video videoUpdate);
	void deleteVideo(Long id);
//	VideoLink videoLink(String url);
	void saveImage(MultipartFile file) throws Exception;
	//String saveImage(MultipartFile file) throws Exception;
	byte[] getByPath(String path) throws Exception;
}
