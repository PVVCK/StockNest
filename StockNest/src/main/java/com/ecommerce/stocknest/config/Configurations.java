package com.ecommerce.stocknest.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;

@Getter
@Configuration
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
}
