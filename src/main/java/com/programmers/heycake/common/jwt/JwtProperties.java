package com.programmers.heycake.common.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
	private String issuer;
	private String clientSecret;
	private int tokenExpire;
	private int refreshTokenExpire;
}
