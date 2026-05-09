package com.java.school.online_video_training.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.java.school.online_video_training.entity.Video;
import com.java.school.online_video_training.projection.VideoReportProjection;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long>, JpaSpecificationExecutor<Video> {
	// String findByImageCover(String path);
	// Video findByCoverImage(String coverImage);
	List<Video> findByCourseId(Long courseId);

//	@Query("SELECT v.title AS title, c.name AS courseName, v.userCreated AS userCreated, " +
//		       "v.userModified AS userModified, v.dateCreated AS dateCreated, v.dateModified AS dateModified " +
//		       "FROM Video v JOIN v.course c ORDER BY v.dateCreate DESC")
//	List<VideoReportProjection> findAllVideoDetails();

	@Query("SELECT v.title AS title, c.name AS courseName, " + "v.userCreated AS userCreated, "
			+ "v.userModified AS userModified, " + "v.dateCreated AS dateCreated, " + "v.dateModified AS dateModified "
			+ "FROM Video v JOIN v.course c " + "ORDER BY v.dateCreated DESC")
	List<VideoReportProjection> findAllVideoDetails();

}
