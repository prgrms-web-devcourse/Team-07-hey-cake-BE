package com.programmers.heycake.domain.member.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.programmers.heycake.domain.order.model.entity.CakeInfo;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;

import lombok.Builder;

@Builder
public record OrderGetDetailResponse(
		Long orderId,
		Long memberId,
		String title,
		String region,
		CakeInfo cakeInfo,
		List<String> images,
		OrderStatus orderStatus,
		int hopePrice,
		int offerCount,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime visitDate,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime createdAt,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime updatedAt
) {
}
