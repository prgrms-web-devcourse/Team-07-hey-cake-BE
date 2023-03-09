package com.programmers.heycake.domain.order.model.dto.request;

import javax.validation.constraints.Positive;

import com.programmers.heycake.domain.order.model.vo.OrderStatus;

public record MyOrderRequest(
		@Positive(message = "커서 id 는 양수여야합니다.")
		Long cursorId,

		@Positive(message = "페이지 사이즈는 양수여야합니다.")
		Integer pageSize,

		OrderStatus orderStatus
) {
}
