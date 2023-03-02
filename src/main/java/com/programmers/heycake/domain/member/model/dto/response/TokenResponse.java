package com.programmers.heycake.domain.member.model.dto.response;

public record TokenResponse(
		String token,
		String refreshToken
) {
}
