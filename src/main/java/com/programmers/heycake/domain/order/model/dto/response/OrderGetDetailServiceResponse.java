package com.programmers.heycake.domain.order.model.dto.response;

import java.time.LocalDateTime;

import com.programmers.heycake.domain.order.model.entity.CakeInfo;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;

import lombok.Builder;

@Builder
public record OrderGetDetailServiceResponse(
		Long orderId,
		Long memberId,
		String title,
		String region,
		CakeInfo cakeInfo,
		OrderStatus orderStatus,
		int hopePrice,
		int offerCount,
		LocalDateTime visitDate,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}