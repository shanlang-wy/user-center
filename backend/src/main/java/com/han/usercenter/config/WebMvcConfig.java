package com.han.usercenter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 头像保存路径
     */
    @Value("${upload.avatar-path:upload/avatar/}")
    private String avatarSavePath;

    /**
     * 头像访问路径前缀
     */
    @Value("${upload.avatar-url-prefix:/upload/avatar/}")
    private String avatarUrlPrefix;

    /**
     * 配置静态资源映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /upload/avatar/** 映射到本地文件系统
        registry.addResourceHandler(normalizeUrlPrefix(avatarUrlPrefix) + "**")
                .addResourceLocations(getFileResourceLocation(avatarSavePath));
    }

    private String normalizeUrlPrefix(String prefix) {
        String normalizedPrefix = prefix == null ? "/upload/avatar/" : prefix;
        if (!normalizedPrefix.startsWith("/")) {
            normalizedPrefix = "/" + normalizedPrefix;
        }
        if (!normalizedPrefix.endsWith("/")) {
            normalizedPrefix = normalizedPrefix + "/";
        }
        return normalizedPrefix;
    }

    private String getFileResourceLocation(String path) {
        String location = new File(path).getAbsoluteFile().toURI().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        return location;
    }

    /**
     * 配置跨域
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有接口，允许前端跨域访问
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}

