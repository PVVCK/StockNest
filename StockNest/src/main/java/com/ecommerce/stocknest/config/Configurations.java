package com.ecommerce.stocknest.config;

import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.Getter;

@Getter
@Configuration
@EnableCaching
@EnableAsync
@EnableScheduling
public class Configurations {

	@Bean
     ModelMapper modelMapper() {
        return new ModelMapper();
    }
	
	@Bean
	 RestTemplate restTemplate()
	{
		return new RestTemplate();
	}
	
	 @Bean
	 CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)  // Cache expiry
                .maximumSize(100));                     // Maximum cache size
        return cacheManager;
    }
	 
	 
}
