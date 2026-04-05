package com.example.spring_project.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;

/**
 * Prevents Redis cache deserialization errors from breaking the whole endpoint.
 * If cache get fails, Spring will fall back to executing the method normally.
 */
@Slf4j
@Component
public class RedisCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Cache GET error (cache={}, key={}): {}", cache != null ? cache.getName() : "unknown", key, exception.toString());
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.warn("Cache PUT error (cache={}, key={}): {}", cache != null ? cache.getName() : "unknown", key, exception.toString());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Cache EVICT error (cache={}, key={}): {}", cache != null ? cache.getName() : "unknown", key, exception.toString());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.warn("Cache CLEAR error (cache={}): {}", cache != null ? cache.getName() : "unknown", exception.toString());
    }
}

