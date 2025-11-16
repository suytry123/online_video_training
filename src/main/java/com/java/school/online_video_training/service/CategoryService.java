package com.java.school.online_video_training.service;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.java.school.online_video_training.dto.CategoryDTO;

public interface CategoryService {
	
	//Category create (Category category);
	CategoryDTO create(CategoryDTO dto);
	CategoryDTO getById(Long id);
	CategoryDTO update(Long id, CategoryDTO categoryUpdate);
//	List<Category> getCategories(String name);
	Page<CategoryDTO> getCategories(Map<String, String> params);
	void deleteById(Long id);
}
