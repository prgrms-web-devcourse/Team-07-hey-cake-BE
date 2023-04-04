package com.programmers.heycake.domain.member.model.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Getter;

@Getter
@RedisHash(value = "token", timeToLive = 36000)
public class Token implements Serializable {

	@Id
	private String refreshToken;

	@Indexed
	private Long memberId;

	public Token(Long memberId, String refreshToken) {
		this.memberId = memberId;
		this.refreshToken = refreshToken;
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
