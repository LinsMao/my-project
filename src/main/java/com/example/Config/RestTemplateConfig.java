package com.example.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // 连接超时：5秒
        factory.setConnectTimeout(5000);
        
        // 读取超时：60秒（AI响应可能较慢，给足够时间）
        factory.setReadTimeout(60000);
        
        return new RestTemplate(factory);
    }
}
