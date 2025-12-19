package com.example.spring_project.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);


        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)


                .withCacheConfiguration("categories",
                        defaultConfig.entryTtl(Duration.ofHours(24)))


                .withCacheConfiguration("courses",
                        defaultConfig.entryTtl(Duration.ofHours(1)))


                .withCacheConfiguration("users",
                        defaultConfig.entryTtl(Duration.ofMinutes(30)))


                .withCacheConfiguration("enrollments:my",
                        defaultConfig.entryTtl(Duration.ofMinutes(10)))


                .withCacheConfiguration("word:ofTheDay",
                        defaultConfig.entryTtl(Duration.ofHours(24)))


                .withCacheConfiguration("dashboard:admin",
                        defaultConfig.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration("dashboard:student",
                        defaultConfig.entryTtl(Duration.ofMinutes(5)))


                .withCacheConfiguration("courses:all",
                        defaultConfig.entryTtl(Duration.ofHours(1)))


                .withCacheConfiguration("words:search",
                        defaultConfig.entryTtl(Duration.ofHours(12)))


                .withCacheConfiguration("course:reviews",
                        defaultConfig.entryTtl(Duration.ofMinutes(15)))


                .withCacheConfiguration("modules:byCourse",
                        defaultConfig.entryTtl(Duration.ofHours(1)))


                .withCacheConfiguration("lessons:byModule",
                        defaultConfig.entryTtl(Duration.ofHours(1)))


                .withCacheConfiguration("friends",
                        defaultConfig.entryTtl(Duration.ofMinutes(5)))


                .withCacheConfiguration("wishlist",
                        defaultConfig.entryTtl(Duration.ofMinutes(10)))


                .withCacheConfiguration("roles",
                        defaultConfig.entryTtl(Duration.ofHours(24)))
                .withCacheConfiguration("permissions",
                        defaultConfig.entryTtl(Duration.ofHours(24)))

                .build();
    }
}
