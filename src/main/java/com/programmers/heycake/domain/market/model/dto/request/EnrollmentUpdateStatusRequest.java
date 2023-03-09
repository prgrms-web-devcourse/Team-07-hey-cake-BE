package com.programmers.heycake.domain.market.model.dto.request;

import javax.validation.constraints.NotNull;

import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;

public record EnrollmentUpdateStatusRequest(@NotNull EnrollmentStatus status) {
}
