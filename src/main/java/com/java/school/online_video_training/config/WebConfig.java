package com.java.school.online_video_training.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOriginPatterns("http://localhost:4200", "http://localhost:4201",
				"http://localhost:8080");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/test/**").addResourceLocations("file:///./ext-resources/").setCachePeriod(0);
	}

//	 @Override
//	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//	        registry.addResourceHandler("/favicon.ico")
//	                .addResourceLocations("classpath:/static/")
//	                .resourceChain(true)
//	                .addResolver(new PathResourceResolver() {
//	                    @Override
//	                    protected Resource getResource(String resourcePath, Resource location) {
//	                        try {
//	                            Resource requestedResource = location.createRelative(resourcePath);
//	                            return requestedResource.exists() && requestedResource.isReadable() ? 
//	                                   requestedResource : new ClassPathResource("/static/favicon.ico");
//	                        } catch (Exception e) {
//	                            return null;
//	                        }
//	                    }
//	                });
//	    }
}
