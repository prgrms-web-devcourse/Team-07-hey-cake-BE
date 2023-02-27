package com.programmers.heycake.domain.member.model.vo;

public record TokenResponse(
		String token,
		String refreshToken
) {
}
