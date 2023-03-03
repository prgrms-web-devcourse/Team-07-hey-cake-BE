package com.programmers.heycake.domain.market.model.dto;

import javax.validation.constraints.NotNull;

import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;

public record EnrollmentStatusRequest(@NotNull EnrollmentStatus status) {
}
