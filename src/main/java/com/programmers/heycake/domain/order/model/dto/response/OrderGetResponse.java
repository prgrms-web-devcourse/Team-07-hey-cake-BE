package com.programmers.heycake.domain.order.model.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.programmers.heycake.domain.order.model.entity.CakeInfo;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;

import lombok.Builder;

@Builder
public record OrderGetResponse(
		Long orderId,
		Long memberId,
		String title,
		String region,
		OrderStatus orderStatus,
		int hopePrice,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime visitDate,
		CakeInfo cakeInfo,
		String requirements,
		int offerCount,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime createdAt,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime updatedAt
) {
}
