package com.java.school.online_video_training.spec;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.java.school.online_video_training.entity.Video;

import lombok.Data;

@Data
public class LinkSpec implements Specification<Video>{
	private final LinkFilter linkFilter;
	
	List<Predicate> predicates = new ArrayList<>(); 
	
	@Override
	public Predicate toPredicate(Root<Video> link, CriteriaQuery<?> query, CriteriaBuilder cb) {
		if(linkFilter.getLinkVideo() != null) {
			//Join<Video, String> linkJoin = link.join("videoLink");
			Join<Video, String> linkJoin = link.join("videoLink", JoinType.INNER);
			//Predicate links = cb.like(cb.upper(link.get("linkVideo")),"%" + linkFilter.getLinkVideo().toLowerCase() + "%");
			Predicate links = cb.like(cb.upper(linkJoin),"%" + linkFilter.getLinkVideo().toLowerCase() + "%");
			predicates.add(links);
		}
		return cb.and(predicates.toArray(Predicate[]::new));
	}
}
