package com.programmers.heycake.domain.order.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record MyOrderResponseList(
		//TODO 나중에 변경
		List<MyOrderResponse> myOrderResponseList,
		// List<OrderDtoWithImage> myOrderResponseList,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddTHH:mm:ss", timezone = "Asia/Seoul")
		LocalDateTime lastTime
) {
}
