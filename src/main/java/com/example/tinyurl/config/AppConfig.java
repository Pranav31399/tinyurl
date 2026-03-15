package com.example.tinyurl.config;

import com.example.tinyurl.repository.InMemoryUrlRepository;
import com.example.tinyurl.repository.UrlRepository;
import com.example.tinyurl.service.IdGenerator;
import com.example.tinyurl.service.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UrlRepository urlRepository() {
        return new InMemoryUrlRepository();
    }

    @Bean
    public IdGenerator idGenerator(@Value("${tinyurl.worker-id:1}") long workerId) {
        return new SnowflakeIdGenerator(workerId);
    }
}
