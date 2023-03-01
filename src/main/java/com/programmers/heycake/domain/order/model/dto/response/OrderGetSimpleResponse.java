package com.programmers.heycake.domain.order.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.order.model.entity.CakeInfo;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;

import lombok.Builder;

@Builder
public record OrderGetSimpleResponse(
		Long orderId,
		String title,
		String region,
		CakeInfo cakeInfo,
		List<String> images,
		OrderStatus orderStatus,
		int hopePrice,
		LocalDateTime createdAt
) {
}