package com.example.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {



    //首页轮播图
    @Value("file:D:/my-images/banner/")
    private String bannerImageLocalPath;

    @Value("${app.image.banner-path}")
    private String bannerImageWebPath;

    // 首页商品
    @Value("file:D:/my-images/product/")
    private String productImageLocalPath;

    @Value("${app.image.product-path}")
    private String productImageWebPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射本地文件夹到web访问路径
        registry.addResourceHandler(bannerImageWebPath + "**")
                .addResourceLocations(bannerImageLocalPath);

        registry.addResourceHandler(productImageWebPath + "**")
                .addResourceLocations(productImageLocalPath);
    }




}
