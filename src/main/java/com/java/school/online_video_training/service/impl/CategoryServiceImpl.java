package com.java.school.online_video_training.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.entity.Category;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.repository.CategoryRepository;
import com.java.school.online_video_training.service.CategoryService;
import com.java.school.online_video_training.service.util.PageUtil;
import com.java.school.online_video_training.spec.CategoryFilter;
import com.java.school.online_video_training.spec.CategorySpec;

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
			.orElseThrow(() -> new ResourceNotFoundException("Category", id));
	}

	@Override
	public Category update(Long id, Category categoryUpdate) {
		Category category = getById(id);
		category.setName(categoryUpdate.getName());
		return categoryRepository.save(category);
	}

//	@Override
//	public List<Category> getCategories(String name) {
//		return categoryRepository.findByNameContaining(name);
//	}

	@Override
	public Page<Category> getCategories(Map<String, String> params) {
		CategoryFilter categoryFilter = new CategoryFilter();
		
		if(params.containsKey("name")) {
			String name = params.get("name");
			categoryFilter.setName(name);
		}
		
		if(params.containsKey("id")) {
			String id = params.get("id");
			categoryFilter.setId(Long.parseLong(id));
		}
		
		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
		if(params.containsKey(PageUtil.PAGE_LIMIT)) {
			pageLimit = Integer.parseInt(params.get(PageUtil.PAGE_LIMIT));
		}
		
		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
		if(params.containsKey(PageUtil.PAGE_NUMBER)){
			pageNumber = Integer.parseInt(params.get(PageUtil.PAGE_NUMBER));
		}
		
		CategorySpec categorySpec = new CategorySpec(categoryFilter);
		
		 Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);
		
		 Page<Category> page = categoryRepository.findAll(categorySpec, pageable);
		 return page;
	}

	@Override
	public void deleteById(Long id) {
//		Category byId = getById(id);
		categoryRepository.deleteById(id);
	}



}