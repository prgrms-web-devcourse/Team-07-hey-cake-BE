package com.programmers.heycake.domain.member.model.dto.response;

public record TokenResponse(
		String accessToken,
		String refreshToken
) {
}
