package com.programmers.heycake.domain.member.model.dto.request;

import javax.validation.constraints.NotBlank;

public record AuthenticationCodeRequest(@NotBlank(message = "인가 코드는 공백일 수 없습니다.") String code) {
}
