package com.programmers.heycake.domain.market.model.dto;

import java.util.List;

public record EnrollmentListResponse(List<EnrollmentListDetailResponse> enrollments, Long nextCursor) {
}
