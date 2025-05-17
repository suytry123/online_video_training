package com.java.school.online_video_training;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OnlineVideoTrainingApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineVideoTrainingApplication.class, args);
	}

}
