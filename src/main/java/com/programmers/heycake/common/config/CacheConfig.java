package com.programmers.heycake.common.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.programmers.heycake.common.cache.CacheType;

@EnableCaching
@Configuration
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		List<CaffeineCache> caffeineCaches = Arrays.stream(CacheType.values())
				.map(cache -> new CaffeineCache(
						cache.getName(),
						Caffeine.newBuilder()
								.expireAfterWrite(cache.getExpireAfterWrite())
								.maximumSize(cache.getMaximumSize())
								.recordStats()
								.build()
				))
				.toList();

		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(caffeineCaches);

		return cacheManager;
	}
}
