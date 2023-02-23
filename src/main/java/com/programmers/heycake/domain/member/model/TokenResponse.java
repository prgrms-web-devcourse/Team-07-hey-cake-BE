package com.programmers.heycake.domain.member.model;

public record TokenResponse(
		String token,
		String refreshToken
) {
}
