package com.programmers.heycake.domain.market.model.dto;

import java.util.List;

public record EnrollmentListResponse(List<EnrollmentListInfoResponse> enrollments, Long nextCursor) {
}
