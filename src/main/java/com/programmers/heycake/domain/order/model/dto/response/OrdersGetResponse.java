package com.programmers.heycake.domain.order.model.dto.response;

import java.util.List;

public record OrdersGetResponse(
		List<OrderGetSimpleServiceResponse> content
) {
}
