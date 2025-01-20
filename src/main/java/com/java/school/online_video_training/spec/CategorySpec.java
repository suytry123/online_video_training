package com.java.school.online_video_training.spec;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.java.school.online_video_training.entity.Category;

import lombok.Data;

@Data
public class CategorySpec implements Specification<Category>{
	
	private final CategoryFilter categoryFilter;
	
	List<Predicate> predicates = new ArrayList<>();

	@Override
	public Predicate toPredicate(Root<Category> category, CriteriaQuery<?> query, CriteriaBuilder cb) {
		
		if(categoryFilter.getName() != null) {
			Predicate name = cb.like(cb.upper(category.get("name")),"%" + categoryFilter.getName().toUpperCase() + "%");
			predicates.add(name);
		}
		
		if(categoryFilter.getId() != null) {
			Predicate id = category.get("id").in(categoryFilter.getId());
			predicates.add(id);
		}
		
		return cb.and(predicates.toArray(Predicate[]::new));
	}

}
