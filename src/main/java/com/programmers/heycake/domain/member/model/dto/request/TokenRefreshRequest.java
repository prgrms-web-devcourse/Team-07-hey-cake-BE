package com.programmers.heycake.domain.member.model.dto.request;

import javax.validation.constraints.NotBlank;

public record TokenRefreshRequest(
		@NotBlank(message = "빈값일 수 없습니다.")
		String refreshToken
) {
}
