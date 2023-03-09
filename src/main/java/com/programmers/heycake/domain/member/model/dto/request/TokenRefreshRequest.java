package com.programmers.heycake.domain.member.model.dto.request;

import javax.validation.constraints.NotBlank;

public record TokenRefreshRequest(
		@NotBlank(message = "리프레쉬 토큰은 공백일 수 없습니다.")
		String refreshToken
) {
}
