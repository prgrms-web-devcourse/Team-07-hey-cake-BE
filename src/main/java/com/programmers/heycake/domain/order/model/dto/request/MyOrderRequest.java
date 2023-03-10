package com.programmers.heycake.domain.order.model.dto.request;

import javax.validation.constraints.Positive;

import com.programmers.heycake.domain.order.model.vo.OrderStatus;

public record MyOrderRequest(
		@Positive Long cursorId,
		@Positive Integer pageSize,
		OrderStatus orderStatus
) {
}
