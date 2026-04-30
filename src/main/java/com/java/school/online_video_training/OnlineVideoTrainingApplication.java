package com.java.school.online_video_training;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableJpaAuditing  
public class OnlineVideoTrainingApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineVideoTrainingApplication.class, args);
	}

}