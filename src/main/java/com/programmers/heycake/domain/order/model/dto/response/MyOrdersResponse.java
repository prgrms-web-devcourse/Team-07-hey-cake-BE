package com.programmers.heycake.domain.order.model.dto.response;

import java.util.List;

public record MyOrdersResponse(
		List<MyOrderResponse> myOrdersResponse,
		Long cursorId
) {
}
