package com.eryonix.config;



import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {

   @Override
public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/uploads/images/**")
            .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/images/");
    registry.addResourceHandler("/uploads/videos/**")
            .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/videos/");
    registry.addResourceHandler("/uploads/profiles/**")
            .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/profiles/");
            registry.addResourceHandler("/uploads/chat/**")
            .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/chat/");
}

}
