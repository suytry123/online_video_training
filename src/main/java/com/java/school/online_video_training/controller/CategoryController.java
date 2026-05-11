package com.java.school.online_video_training.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.java.school.online_video_training.mapper.CategoryMapper;
import com.java.school.online_video_training.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/categories")
public class CategoryController {

//	@Autowired
	private final CategoryService categoryService;
	private final CategoryMapper categoryMapper;

	@PreAuthorize("hasAuthority('category:write')")
	@PostMapping
	public ResponseEntity<?> create(@RequestBody CategoryDTO dto) {
		return ResponseEntity.ok(categoryService.create(dto));
	}

	@PreAuthorize("hasAuthority('category:read')")
	@GetMapping("{id}")
	public ResponseEntity<?> getOneCategory(@PathVariable("id") Long categoryId) {
		CategoryDTO category = categoryService.getById(categoryId);
		return ResponseEntity.ok(category);
	}

	@PreAuthorize("hasAuthority('category:write')")
	@PutMapping("{id}")
	public ResponseEntity<?> update(@PathVariable("id") Long categoryId, @RequestBody CategoryDTO categoryDTO) {
		CategoryDTO updatedCategory = categoryService.update(categoryId, categoryDTO);
		return ResponseEntity.ok(updatedCategory);
	}

	@PreAuthorize("hasAuthority('category:read')")
	@GetMapping
	public ResponseEntity<?> getCategories(@RequestParam Map<String, String> params) {
		Page<CategoryDTO> page = categoryService.getCategories(params);

		PageDTO pageDTO = new PageDTO(page);

		return ResponseEntity.ok(pageDTO);
	}
	
	@PreAuthorize("hasAuthority('category:write')")
	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteById(@PathVariable("id") Long categoryId) {
		categoryService.deleteById(categoryId);
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasAuthority('category:read')")
	@GetMapping("/trash")
	public ResponseEntity<?> getTrash() {

		return ResponseEntity.ok(categoryService.getTrash());

	}

	@PreAuthorize("hasAuthority('category:write')")
	@PutMapping("/{id}/restore")
	public ResponseEntity<?> restore(@PathVariable Long id) {

		categoryService.restore(id);

		return ResponseEntity.ok().build();

	}
}
