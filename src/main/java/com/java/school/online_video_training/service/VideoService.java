package com.java.school.online_video_training.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.java.school.online_video_training.dto.VideoDTO;
import com.java.school.online_video_training.dto.VideoResponseDTO;

public interface VideoService {
	VideoResponseDTO createVideo(VideoDTO video);
	//Video createVideo(MultipartFile file,Video video)throws Exception;
	VideoResponseDTO getVideoById(Long id);
	List<VideoResponseDTO> getVideosByCourse(Long courseId);
	Page<VideoResponseDTO> getVideos(Map<String, String> video);
	VideoResponseDTO updateVideo(Long id, VideoDTO videoUpdate);
	void deleteVideo(Long id);
//	VideoLink videoLink(String url);
//	void saveImage(Long id, MultipartFile file) throws Exception;
	//String saveImage(MultipartFile file) throws Exception;
	//byte[] getByPath(String path) throws Exception;
//	byte[] getImageCoverById(Long id) throws Exception;
//	void updateImage(String path, MultipartFile file) throws Exception;
//	void updateImage(Long id, MultipartFile file) throws Exception;
//	Page<Map<String, String>> getImages(Map<String, String> images);
//	Page<String> getImages(Map<String, String> image) throws Exception;
//	void deleteImageByPath(String url) throws Exception;
//	void deleteImageById(Long id) throws Exception;
//	void videoLink(Long id, List<String> link);
//	String getLink(Long id);
//	Page<String> getLinks(Map<String, String> link);
//	void updateLink(Long id, List<String> link);
//	void deleteLink(Long id);
	Page<VideoResponseDTO> getVideosForUser(Long courseId, Long userId, Map<String, String> params);
	List<VideoResponseDTO> getTrash();
	void restore(Long id);


}
