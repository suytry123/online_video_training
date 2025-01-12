package com.java.school.online_video_training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.java.school.online_video_training.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
	
}
