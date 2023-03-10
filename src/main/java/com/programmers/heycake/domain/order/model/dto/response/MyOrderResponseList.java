package com.programmers.heycake.domain.order.model.dto.response;

import java.util.List;

public record MyOrderResponseList(
		List<MyOrderResponse> myOrderResponseList,
		Long cursorId
) {
}
