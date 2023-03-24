package com.programmers.heycake.domain.market.model.dto.response;

import java.util.List;

public record EnrollmentsResponse(List<EnrollmentsComponentResponse> enrollments, Long nextCursor) {
}
