package com.demo.ricospringkafka;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(value= "simple")
public class SimpleExampleConfig {
	
	@Bean
	public Function<String, String> uppercase(){
		return v -> v.toUpperCase();
	}

}
