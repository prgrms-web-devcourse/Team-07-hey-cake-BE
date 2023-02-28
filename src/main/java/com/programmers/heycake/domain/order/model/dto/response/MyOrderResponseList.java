package com.programmers.heycake.domain.order.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record MyOrderResponseList(
		List<GetOrderResponse> getOrderResponseList,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		LocalDateTime lastTime
) {
}
