package com.programmers.heycake.domain.market.model.dto.response;

import java.util.List;

public record EnrollmentsResponse(List<EnrollmentsElementResponse> enrollments, Long nextCursor) {
}
