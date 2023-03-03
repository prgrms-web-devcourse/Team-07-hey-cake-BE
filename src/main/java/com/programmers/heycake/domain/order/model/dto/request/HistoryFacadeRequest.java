package com.programmers.heycake.domain.order.model.dto.request;

import com.programmers.heycake.domain.order.model.dto.OrderDto;

public record HistoryFacadeRequest(
		Long memberId,
		Long marketId,
		OrderDto orderDto
) {
}
