package com.java.school.online_video_training.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class ForceJsonConfig implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(converter ->
            !(converter instanceof MappingJackson2HttpMessageConverter)
            || converter instanceof MappingJackson2XmlHttpMessageConverter
        );
    }

    @Bean
    public WebMvcConfigurer removeXmlConverter() {
        return new WebMvcConfigurer() {
            @Override
            public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
                converters.removeIf(c -> c instanceof MappingJackson2XmlHttpMessageConverter);
            }
        };
    }
}
