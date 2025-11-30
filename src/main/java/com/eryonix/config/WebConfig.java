package com.eryonix.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {

   @Override
public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // No local resource handlers needed for Cloudinary
    }

}
