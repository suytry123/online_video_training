package com.java.school.online_video_training.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.entity.Category;
import com.java.school.online_video_training.repository.CategoryRepository;
import com.java.school.online_video_training.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{
	
	@Autowired
	private  CategoryRepository categoryRepository;
	
	@Override
	public Category create(Category category) {
		return categoryRepository.save(category);
	}

}
