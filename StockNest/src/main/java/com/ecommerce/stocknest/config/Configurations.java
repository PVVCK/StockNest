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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import com.ecommerce.stocknest.security.JWTFilter;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.Getter;

@Getter
@Configuration
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableWebSecurity
public class Configurations {
	
	private final JWTFilter jwtFilter;

	public Configurations(JWTFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
	
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
	 
	 @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	 
	 @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	            .csrf(csrf -> csrf.disable())  // Disable CSRF for stateless APIs
	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No session management
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/stocknest/auth/**").permitAll()  // Public authentication endpoints (Login, Register)
	                .requestMatchers("/stocknest/product/**", "/api/image/**").permitAll()  // Allow all GET requests (Restricted in Filter)
	                .anyRequest().authenticated() // All other endpoints require authentication
	            )
	            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

	        return http.build();
	    }
	 


	    @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	        return authenticationConfiguration.getAuthenticationManager();
	    }
	 
	 
}
