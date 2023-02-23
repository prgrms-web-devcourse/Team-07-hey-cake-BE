package com.programmers.heycake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.programmers.heycake.common.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = JwtProperties.class)
public class HeyCakeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeyCakeApplication.class, args);
	}

}
