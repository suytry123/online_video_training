package com.java.school.online_video_training.service;

import java.util.List;
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
	void saveImage(Long id, MultipartFile file) throws Exception;
	//String saveImage(MultipartFile file) throws Exception;
	byte[] getByPath(String path) throws Exception;
	void updateImage(String path, MultipartFile file) throws Exception;
	Page<String> getImages(Map<String, String> image) throws Exception;
//	Page<String> getImages(Map<String, String> image) throws Exception;
	void deleteImageByPath(String url) throws Exception;
	void videoLink(Long id, List<String> link);
	String getLink(Long id);
	String updateLink(Long id, List<String> link);
}
