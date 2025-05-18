package com.java.school.online_video_training.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
	@Bean(name = "taskExecutor", destroyMethod = "shutdown")
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10); // Increased for better concurrency
		executor.setMaxPoolSize(50); // Increased for better concurrency
		executor.setQueueCapacity(500); // Increased for better concurrency
		executor.setThreadNamePrefix("AsyncEmail-");
		executor.initialize();
		return executor;
	}
}
