package com.programmers.heycake.domain.order.model.dto.response;

import java.util.List;

public record OrderGetSimpleResponses(
		List<OrderGetSimpleResponse> content,
		Long cursorId,
		Boolean isLast
) {
}