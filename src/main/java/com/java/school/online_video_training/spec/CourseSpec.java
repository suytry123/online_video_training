package com.java.school.online_video_training.spec;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.java.school.online_video_training.entity.Course;

import lombok.Data;

@Data
public class CourseSpec implements Specification<Course>{
	
	private final CourseFilter courseFilter;
	
	List<Predicate> predicates = new ArrayList<>();
	@Override
	public Predicate toPredicate(Root<Course> course, CriteriaQuery<?> query, CriteriaBuilder cb) {
		if(courseFilter.getName() != null) {
			Predicate name = cb.like(cb.upper(course.get("name")),"%" + courseFilter.getName().toUpperCase() + "%");
			predicates.add(name);
		}
		
		if(courseFilter.getCategoryId() != null) {
			Predicate id = course.get("categoryId").in(courseFilter.getCategoryId());
			predicates.add(id);
		}
		
		Predicate isDeleted = cb.isFalse(course.get("isDeleted"));
		predicates.add(isDeleted);
		
		return cb.and(predicates.toArray(Predicate[]::new));
	}

}
