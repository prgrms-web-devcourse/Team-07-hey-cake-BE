package com.programmers.heycake.domain.market.model.dto;

import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;

public record MarketEnrollmentListRequest(Long cursorEnrollmentId, Integer pageSize, EnrollmentStatus status) {
}
