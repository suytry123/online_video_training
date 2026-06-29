package com.java.school.online_video_training.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.dto.CategoryDTO;
import com.java.school.online_video_training.entity.Category;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.mapper.CategoryMapper;
import com.java.school.online_video_training.repository.CategoryRepository;
import com.java.school.online_video_training.service.CategoryService;
import com.java.school.online_video_training.service.util.PageUtil;
import com.java.school.online_video_training.spec.CategoryFilter;
import com.java.school.online_video_training.spec.CategorySpec;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	private final CategoryMapper categoryMapper;

	@Override
	public CategoryDTO create(CategoryDTO dto) {
		Category category = categoryMapper.toCategory(dto);
		Category savedCategory = categoryRepository.save(category);
		return categoryMapper.toCategoryDTO(savedCategory);
	}

	@Override
	public CategoryDTO getById(Long id) {
		Category category = categoryRepository.findById(id)
				.filter(c -> !c.isDeleted())
				.orElseThrow(() -> new ResourceNotFoundException("Category", id));
		return categoryMapper.toCategoryDTO(category);
	}

	@Override
	public CategoryDTO update(Long id, CategoryDTO categoryUpdate) {
		Category category = categoryRepository.findById(id)
				.filter(c -> !c.isDeleted())
				.orElseThrow(() -> new ResourceNotFoundException("Category", id));
		category.setName(categoryUpdate.getName());
		Category updated = categoryRepository.save(category);
		return categoryMapper.toCategoryDTO(updated);
	}

	@Override
	public Page<CategoryDTO> getCategories(Map<String, String> params) {
		CategoryFilter categoryFilter = new CategoryFilter();
		if (params.containsKey("name")) {
			String name = params.get("name");
			categoryFilter.setName(name);
		}
		if (params.containsKey("id")) {
			String id = params.get("id");
			categoryFilter.setId(Long.parseLong(id));
		}
		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
		if (params.containsKey(PageUtil.PAGE_LIMIT)) {
			pageLimit = Integer.parseInt(params.get(PageUtil.PAGE_LIMIT));
		}
		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
		if (params.containsKey(PageUtil.PAGE_NUMBER)) {
			pageNumber = Integer.parseInt(params.get(PageUtil.PAGE_NUMBER));
		}
		
		CategorySpec categorySpec = new CategorySpec(categoryFilter);
		Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);
		Page<Category> page = categoryRepository.findAll(categorySpec, pageable);
		return page.map(categoryMapper::toCategoryDTO);
	}

	@Override
	public void deleteById(Long id) {
//		Category byId = getById(id);
//		categoryRepository.deleteById(id);
		Category category = getEntityById(id); 
		category.setDeleted(true); 
		categoryRepository.save(category);
	}
	
	private Category getEntityById(Long id) {
		return categoryRepository.findById(id).filter(c -> !c.isDeleted())
				.orElseThrow(() -> new ResourceNotFoundException("Category", id));
	}
	
	@Override
	public List<CategoryDTO> getTrash() {
		List<Category> categories = categoryRepository.findByIsDeletedTrue();

		return categories.stream().map(categoryMapper::toCategoryDTO).toList();

	}

	@Override
	public void restore(Long id) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category", id));

		category.setDeleted(false);

		categoryRepository.save(category);

	}


//	@Override
//	public List<Category> getCategories(String name) {
//		return categoryRepository.findByNameContaining(name);
//	}

	/*
	 * @Override public Page<CategoryDTO> getCategories(Map<String, String> params)
	 * { CategoryFilter categoryFilter = new CategoryFilter();
	 * 
	 * if(params.containsKey("name")) { String name = params.get("name");
	 * categoryFilter.setName(name); }
	 * 
	 * if(params.containsKey("id")) { String id = params.get("id");
	 * categoryFilter.setId(Long.parseLong(id)); }
	 * 
	 * int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
	 * if(params.containsKey(PageUtil.PAGE_LIMIT)) { pageLimit =
	 * Integer.parseInt(params.get(PageUtil.PAGE_LIMIT)); }
	 * 
	 * int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
	 * if(params.containsKey(PageUtil.PAGE_NUMBER)){ pageNumber =
	 * Integer.parseInt(params.get(PageUtil.PAGE_NUMBER)); }
	 * 
	 * CategorySpec categorySpec = new CategorySpec(categoryFilter);
	 * 
	 * Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);
	 * 
	 * Page<CategoryDTO> page = categoryRepository.findAll(categorySpec, pageable);
	 * return page; }
	 */

}