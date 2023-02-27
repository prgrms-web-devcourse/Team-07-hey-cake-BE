package com.programmers.heycake.domain.order.model.vo.request;

import com.programmers.heycake.domain.order.model.entity.Order;

public record HistoryFacadeRequest(
		Long memberId,
		Long marketId,
		//TODO orderDTo로 변경
		Order order
) {
}
