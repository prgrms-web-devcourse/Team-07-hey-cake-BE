package com.programmers.heycake.domain.order.model.dto.request;

import java.time.LocalDateTime;

import javax.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

public record GetOrderRequest(
		@DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
		LocalDateTime cursorTime,
		@Positive
		int pageSize,
		String orderStatus
) {
}
