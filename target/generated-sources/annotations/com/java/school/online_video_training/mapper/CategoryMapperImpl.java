package com.java.school.online_video_training.mapper;

import com.java.school.online_video_training.dto.CategoryDTO;
import com.java.school.online_video_training.entity.Category;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-23T16:12:50+0700",
    comments = "version: 1.5.3.Final, compiler: Eclipse JDT (IDE) 3.39.0.v20240820-0604, environment: Java 21.0.4 (Eclipse Adoptium)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public Category toCategory(CategoryDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Category category = new Category();

        category.setName( dto.getName() );

        return category;
    }

    @Override
    public CategoryDTO toCategoryDTO(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDTO categoryDTO = new CategoryDTO();

        categoryDTO.setName( category.getName() );

        return categoryDTO;
    }
}
