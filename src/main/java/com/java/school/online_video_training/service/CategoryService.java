package com.java.school.online_video_training.service;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.java.school.online_video_training.entity.Category;

public interface CategoryService {
	
	Category create (Category category);
	Category getById(Long id);
	Category update(Long id, Category categoryUpdate);
//	List<Category> getCategories(String name);
	Page<Category> getCategories(Map<String, String> params);
	void deleteById(Long id);
}
