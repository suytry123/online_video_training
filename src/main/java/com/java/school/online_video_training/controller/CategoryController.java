package com.java.school.online_video_training.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.dto.CategoryDTO;
import com.java.school.online_video_training.dto.PageDTO;
import com.java.school.online_video_training.entity.Category;
import com.java.school.online_video_training.mapper.CategoryMapper;
import com.java.school.online_video_training.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("categories")
public class CategoryController {
	
//	@Autowired
	private final CategoryService categoryService;
	private final CategoryMapper categoryMapper;
	
	
	@PostMapping
	public ResponseEntity<?> create(@RequestBody CategoryDTO dto){
		Category category = categoryMapper.toCategory(dto);
		categoryService.create(category);
	    return ResponseEntity.ok(categoryMapper.toCategoryDTO(category));
	}
	
	@GetMapping("{id}")
	public ResponseEntity<?> getOneCategory(@PathVariable("id") Long categoryId){
		Category category = categoryService.getById(categoryId);
		return ResponseEntity.ok(categoryMapper.toCategoryDTO(category));
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> update(@PathVariable("id") Long categoryId, @RequestBody CategoryDTO categoryDTO){
		Category category = categoryMapper.toCategory(categoryDTO);
		Category updatedCategory = categoryService.update(categoryId, category);
		return ResponseEntity.ok(categoryMapper.toCategoryDTO(updatedCategory));
	}
	
	 @GetMapping
	 public ResponseEntity<?> getCategories(@RequestParam Map<String, String> params){
		Page<Category> page = categoryService.getCategories(params);
	    	
	    PageDTO pageDTO = new PageDTO(page);
	    	
		
		return ResponseEntity.ok(pageDTO);
	}
	
	@DeleteMapping
	 public ResponseEntity<?> delete(@RequestParam("id") Long categoryId){
	 	categoryService.deleteById(categoryId);
	 	
	 	return ResponseEntity.ok().build();
		}
}
