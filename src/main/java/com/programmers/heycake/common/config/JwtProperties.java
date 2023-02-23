package com.programmers.heycake.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
@RequiredArgsConstructor
public class JwtProperties {

	private final String clientSecret;
	private final String issuer;
	private final int tokenExpire;
	private final int refreshTokenExpire;
}
