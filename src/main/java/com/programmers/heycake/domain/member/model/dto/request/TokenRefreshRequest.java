package com.programmers.heycake.domain.member.model.dto.request;

import javax.validation.constraints.NotBlank;

public record TokenRefreshRequest(@NotBlank String refreshToken) {
}
