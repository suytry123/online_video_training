package com.java.school.online_video_training.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.dto.CategoryDTO;
import com.java.school.online_video_training.entity.Category;
import com.java.school.online_video_training.mapper.CategoryMapper;
import com.java.school.online_video_training.service.CategoryService;

@RestController
@RequestMapping("categories")
public class CategoryController {
	
	@Autowired
	CategoryService categoryService;
	
	@PostMapping
	public ResponseEntity<?> create(@RequestBody CategoryDTO categoryDTO){
		Category category = CategoryMapper.INSTANCE.toCategory(categoryDTO);
		category = categoryService.create(category);
		return ResponseEntity.ok(category);
	}
}
