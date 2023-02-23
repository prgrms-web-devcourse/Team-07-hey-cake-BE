package com.programmers.heycake.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.programmers.heycake.common.jwt.Jwt;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class JwtConfig {

	private final JwtProperties jwtProperties;

	@Bean
	public Jwt jwt() {
		return new Jwt(
				jwtProperties.getClientSecret(),
				jwtProperties.getIssuer(),
				jwtProperties.getTokenExpire(),
				jwtProperties.getRefreshTokenExpire());
	}
}
