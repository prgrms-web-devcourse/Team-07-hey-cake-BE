package com.programmers.heycake.domain.order.model.dto.response;

import java.time.LocalDateTime;

import com.programmers.heycake.domain.order.model.entity.CakeInfo;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;

import lombok.Builder;

@Builder
public record OrderGetSimpleServiceResponse(
		Long orderId,
		String title,
		String region,
		CakeInfo cakeInfo,
		OrderStatus orderStatus,
		int hopePrice,
		LocalDateTime createdAt
) {
}