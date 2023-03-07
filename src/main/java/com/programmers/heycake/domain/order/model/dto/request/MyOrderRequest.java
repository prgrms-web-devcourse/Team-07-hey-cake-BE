package com.programmers.heycake.domain.order.model.dto.request;

import java.time.LocalDateTime;

import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;

public record MyOrderRequest(
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		LocalDateTime cursorDate,
		@Positive
		int pageSize,
		OrderStatus orderStatus
) {
}
