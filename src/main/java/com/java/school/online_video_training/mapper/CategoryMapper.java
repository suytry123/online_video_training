package com.java.school.online_video_training.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.java.school.online_video_training.dto.CategoryDTO;
import com.java.school.online_video_training.entity.Category;

@Mapper
public interface CategoryMapper {
	CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
	
	Category toCategory(CategoryDTO dto);
	
	CategoryDTO toCategoryDTO(Category category);
}
