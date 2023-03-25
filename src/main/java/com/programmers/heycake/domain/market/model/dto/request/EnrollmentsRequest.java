package com.programmers.heycake.domain.market.model.dto.request;

import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;

public record EnrollmentsRequest(Long cursorEnrollmentId, Integer pageSize, EnrollmentStatus status) {
}
