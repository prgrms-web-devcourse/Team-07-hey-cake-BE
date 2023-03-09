package com.programmers.heycake.domain.member.model.dto.request;

import javax.validation.constraints.NotBlank;

public record AuthenticationCodeRequest(@NotBlank String code) {
}
