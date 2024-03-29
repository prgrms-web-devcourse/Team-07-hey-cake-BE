package com.programmers.heycake.domain.order.model.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.programmers.heycake.domain.order.model.entity.CakeInfo;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;

import lombok.Builder;

@Builder
public record MyOrderResponse(
		Long id,
		String title,
		OrderStatus orderStatus,
		String region,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		LocalDateTime visitTime,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		LocalDateTime createdAt,
		CakeInfo cakeInfo,
		int hopePrice,
		String imageUrl,
		int offerCount
) {
	public MyOrderResponse(Long id, String title, OrderStatus orderStatus, String region, LocalDateTime visitTime,
			LocalDateTime createdAt, CakeInfo cakeInfo, int hopePrice, String imageUrl) {
		this(id, title, orderStatus, region, visitTime, createdAt, cakeInfo, hopePrice, imageUrl, 0);
	}
}
