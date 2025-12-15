package com.guang.resms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthInterceptor;

    public WebConfig(JwtAuthInterceptor jwtAuthInterceptor) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
    }

    @org.springframework.beans.factory.annotation.Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取所有配置的上传路径
        String fullPath;
        File file = new File(uploadPath);
        if (!file.isAbsolute()) {
            fullPath = System.getProperty("user.dir") + File.separator + uploadPath;
        } else {
            fullPath = uploadPath;
        }

        // 规范化路径（Windows下将\转换为/）
        fullPath = fullPath.replace("\\", "/");
        if (!fullPath.endsWith("/")) {
            fullPath += "/";
        }

        // 映射uploads文件夹
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + fullPath);

        // 同时保留默认的静态资源映射
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "classpath:/templates/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/auth/csrf-token",
                        "/uploads/**",
                        "/error",
                        "/favicon.ico");
    }
}
