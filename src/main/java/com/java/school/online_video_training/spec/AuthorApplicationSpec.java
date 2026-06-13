package com.java.school.online_video_training.spec;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.java.school.online_video_training.entity.AuthorApplication;
import com.java.school.online_video_training.entity.User;

public class AuthorApplicationSpec implements Specification<AuthorApplication> {

	private final AuthorApplicationFilter filter;

	public AuthorApplicationSpec(AuthorApplicationFilter filter) {
		this.filter = filter;
	}

	@Override
	public Predicate toPredicate(Root<AuthorApplication> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<>();

		if (filter.getStatus() != null) {
			predicates.add(cb.equal(root.get("status"), filter.getStatus()));
		}

		if (filter.getUsername() != null && !filter.getUsername().isBlank()) {

			Join<AuthorApplication, User> applicantJoin = root.join("applicant");

			predicates.add(
					cb.like(cb.lower(applicantJoin.get("username")), "%" + filter.getUsername().toLowerCase() + "%"));
		}

		return cb.and(predicates.toArray(new Predicate[0]));
	}
}