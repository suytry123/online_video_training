package com.java.school.online_video_training.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**").allowedOriginPatterns("http://localhost:4200", "http://localhost:4201",
//				"http://localhost:8080");
//	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/test/**").addResourceLocations("file:///./ext-resources/").setCachePeriod(0);
	}

	/*@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("http://localhost:4200", "http://localhost:8080").allowedMethods("*")
				.allowedHeaders("*").exposedHeaders("Authorization").allowCredentials(true);
	}*/

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

	    CorsConfiguration config = new CorsConfiguration();

	    config.setAllowedOriginPatterns(List.of(
	    	"http://localhost:8080",
	        "http://localhost:4200",
	        "https://*.ngrok-free.app"
	    ));

	    config.setAllowedMethods(List.of(
	        "GET",
	        "POST",
	        "PUT",
	        "DELETE",
	        "OPTIONS"
	    ));

	    config.setAllowedHeaders(List.of("*"));
	    config.setExposedHeaders(List.of("Authorization"));
	    config.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source =
	            new UrlBasedCorsConfigurationSource();

	    source.registerCorsConfiguration("/**", config);

	    return source;
	}

//	@Bean
//    public StandardServletMultipartResolver multipartResolver() {
//        return new StandardServletMultipartResolver();
//    }

}
