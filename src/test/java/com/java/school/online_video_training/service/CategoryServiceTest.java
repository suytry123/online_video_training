package com.java.school.online_video_training.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.java.school.online_video_training.entity.Category;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.repository.CategoryRepository;
import com.java.school.online_video_training.service.impl.CategoryServiceImpl;
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
	
	@Mock
	private  CategoryRepository categoryRepository;
	
	private CategoryService categoryService;
	
	@BeforeEach
	public void setUp() {
		categoryService = new CategoryServiceImpl(categoryRepository);
	}
//	
//	@Test
//	public void testCreate() {
//		//given
//		Category category = new Category();
//		category.setName("Java");
//		category.setId(1L);
//		
//		//when
//		when(categoryRepository.save(any(Category.class))).thenReturn(category);
//		Category category2 = categoryService.create(new Category());
//		
//		//then
//		assertEquals(1, category2.getId());
//		assertEquals("Java", category2.getName());
//	}
	

	@Test
	public void testCreate() {
		//given
		Category category = new Category();
		category.setName("Java");
		category.setId(1L);
		//when
		categoryService.create(category);
		//then
		verify(categoryRepository, times(1)).save(category);
	}
	
	@Test
	public void testByIdSuccess() {
		//given
		Category category = new Category();
		category.setName("Java");
		category.setId(1L);
		
		//when
			when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
			Category returnCategory = categoryService.getById(1l);
		//then
		assertEquals(1L, returnCategory.getId());
		assertEquals("Java", returnCategory.getName());
	}
	
//	@Test
//	public void testByIdThrow() {
//		//given
//		
//		//when
//		when(categoryRepository.findById(2L)).thenReturn(Optional.empty());
//		//categoryService.getById(2L);
//		assertThatThrownBy(() -> categoryService.getById(2L))
//			.isInstanceOf(ResourceNotFoundException.class)
//			.hasMessage("Category With id = 2 Not Found");
//		
//		//then
//	}
}
