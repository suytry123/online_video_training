package com.java.school.online_video_training.spec;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.entity.Video;

import lombok.Data;

@Data
public class UserSpec implements Specification<User> {
	private final UserFilter filter;

	List<Predicate> predicates = new ArrayList<>();

	@Override
	public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		Predicate predicate = cb.conjunction();
		// Always filter for users with a non-null and non-empty photo
		predicate = cb.and(predicate, cb.isNotNull(root.get("photo")));
		predicate = cb.and(predicate, cb.notEqual(root.get("photo"), ""));
		if (filter.getPhoto() != null && !filter.getPhoto().isEmpty()) {
			predicate = cb.and(predicate, cb.equal(root.get("photo"), filter.getPhoto()));
		}
		// Add more filter conditions if needed
		return predicate;
	}
}
