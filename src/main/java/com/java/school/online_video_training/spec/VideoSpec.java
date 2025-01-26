package com.java.school.online_video_training.spec;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.java.school.online_video_training.entity.Video;

import lombok.Data;

@Data
public class VideoSpec implements Specification<Video>{
	
	private final VideoFilter videoFilter;
	
	List<Predicate> predicates = new ArrayList<>();
	
	@Override
	public Predicate toPredicate(Root<Video> video, CriteriaQuery<?> query, CriteriaBuilder cb) {
		if(videoFilter.getTitle() != null) {
			Predicate name = cb.like(cb.upper(video.get("videoTitle")),"%" + videoFilter.getTitle().toUpperCase() + "%");
			predicates.add(name);
		}
		
		if(videoFilter.getCourseId() != null) {
			Predicate id = video.get("categoryId").in(videoFilter.getCourseId());
			predicates.add(id);
		}
		
		return cb.and(predicates.toArray(Predicate[]::new));
	}
}
