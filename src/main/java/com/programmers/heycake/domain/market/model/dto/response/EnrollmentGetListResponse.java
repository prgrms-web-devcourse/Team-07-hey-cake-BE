package com.programmers.heycake.domain.market.model.dto.response;

import java.util.List;

public record EnrollmentGetListResponse(List<EnrollmentListSummaryWithImageResponse> enrollments, Long nextCursor) {
}
