package com.ecommerce.stocknest.config;

import java.util.List;
import java.util.concurrent.TimeUnit;
import io.swagger.v3.oas.models.info.Info;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ecommerce.stocknest.interceptor.JWTInterceptor;
import com.ecommerce.stocknest.security.JWTFilter;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Getter;


@Getter
@Configuration
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableWebSecurity
public class Configurations implements WebMvcConfigurer {

    private final JWTInterceptor jwtInterceptor;

    @Value("${security.enabled}")
    private boolean securityEnabled;

    public Configurations(JWTInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100));
        return cacheManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ✅ Exclude Swagger-related URLs from security
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/swagger-ui.html",
                    "/webjars/**"
                ).permitAll()

                // ✅ Public API endpoints
                .requestMatchers("/stocknest/auth/**").permitAll()
                .requestMatchers("/stocknest/product/**", "/stocknest/image/**").permitAll()

                // ✅ All other APIs require authentication
                .anyRequest().authenticated()
            )
            // ⬇️ Add your JWTFilter **before** UsernamePasswordAuthenticationFilter
            .addFilterBefore(new JWTFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // Exclude Swagger from Interceptor
//        registry.addInterceptor(jwtInterceptor)
//            .addPathPatterns("/stocknest/**")
//            .excludePathPatterns(
//                "/stocknest/auth/**",
//                "/stocknest/swagger-ui/**",
//                "/stocknest/v3/api-docs/**",
//                "/stocknest/swagger-resources/**",
//                "/stocknest/swagger-ui.html",
//                "/stocknest/webjars/**"
//            );
//    }
    
//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .components(new Components()
//                    .addSecuritySchemes("bearerAuth", new SecurityScheme()
//                        .type(SecurityScheme.Type.HTTP)
//                        .scheme("bearer")
//                        .bearerFormat("JWT"))
//                )
//                .security(List.of(new SecurityRequirement().addList("bearerAuth")));
//    }
//    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("StockNest Swagger UI")  // Set the tab title here
                    .version("1.0.0")
                    .description("API documentation for StockNest application"))
                .components(new Components()
                    .addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .security(List.of(new SecurityRequirement().addList("bearerAuth")));
    }
    
}
