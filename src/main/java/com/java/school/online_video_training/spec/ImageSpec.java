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
public class ImageSpec implements Specification<byte[]>{
	private final ImageFilter imageFilter;
	
	List<Predicate> predicates = new ArrayList<>();
	@Override
	public Predicate toPredicate(Root<byte[]> image, CriteriaQuery<?> query, CriteriaBuilder cb) {
		if(imageFilter.getPath() != null) {
			Predicate name = cb.like(cb.upper(image.get("imageCover")),"%" + imageFilter.getPath().toUpperCase() + "%");
			predicates.add(name);
		}
		
		return cb.and(predicates.toArray(Predicate[]::new));
	}
}
