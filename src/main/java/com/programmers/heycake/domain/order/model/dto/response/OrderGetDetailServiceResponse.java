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
		OrderStatus orderStatus,
		int hopePrice,
		LocalDateTime visitDate,
		CakeInfo cakeInfo,
		int offerCount,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
