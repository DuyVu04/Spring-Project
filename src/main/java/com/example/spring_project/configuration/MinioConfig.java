package com.example.spring_project.configuration;

import io.minio.MinioClient;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Slf4j
@Configuration
public class MinioConfig {
    @Value("${minio.url}")
    private String url;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    private final Logger log = LoggerFactory.getLogger(MinioConfig.class);
    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(url)
                    .credentials(accessKey, secretKey).build();
            log.info("Creating minio client successful , url: {}",url);
            return client;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create minio client", e);
        }
    }
}
