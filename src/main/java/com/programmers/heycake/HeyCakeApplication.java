package com.programmers.heycake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties
public class HeyCakeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeyCakeApplication.class, args);
	}

}
