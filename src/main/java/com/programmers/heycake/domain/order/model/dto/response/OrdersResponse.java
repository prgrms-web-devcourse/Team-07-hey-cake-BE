package com.programmers.heycake.domain.order.model.dto.response;

import java.util.List;

public record OrdersResponse(
		List<OrdersElementResponse> content,
		Long cursorId,
		Boolean isLast
) {
}