package com.programmers.heycake.domain.market.model.dto;

import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;

public record EnrollmentListRequest(Long cursorEnrollmentId, Integer pageSize, EnrollmentStatus status) {
}
