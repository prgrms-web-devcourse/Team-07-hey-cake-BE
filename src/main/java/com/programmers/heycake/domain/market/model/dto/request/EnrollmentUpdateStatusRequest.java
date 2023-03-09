package com.programmers.heycake.domain.market.model.dto.request;

import javax.validation.constraints.NotNull;

import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;

public record EnrollmentUpdateStatusRequest(@NotNull(message = "변경 상태 값은 필수입니다.") EnrollmentStatus status) {
}
