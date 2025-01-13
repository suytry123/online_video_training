package com.java.school.online_video_training.service;

import java.util.List;

import com.java.school.online_video_training.entity.Category;

public interface CategoryService {
	
	Category create (Category category);
	Category getById(Long id);
	Category update(Long id, Category categoryUpdate);
}
