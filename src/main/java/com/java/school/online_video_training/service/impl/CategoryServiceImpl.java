package com.java.school.online_video_training.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.entity.Category;
import com.java.school.online_video_training.exception.ApiException;
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

	@Override
	public Category getById(Long id) {
		return categoryRepository.findById(id)
			.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,String.format( "Category with id = %d not found")));
	}

	@Override
	public Category update(Long id, Category categoryUpdate) {
		Category category = getById(id);
		category.setName(categoryUpdate.getName());
		return categoryRepository.save(category);
	}



}