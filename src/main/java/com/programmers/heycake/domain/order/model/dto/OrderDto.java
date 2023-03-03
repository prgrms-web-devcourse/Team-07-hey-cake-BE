package com.programmers.heycake.domain.order.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.programmers.heycake.domain.offer.model.dto.OfferDto;
import com.programmers.heycake.domain.order.model.entity.CakeInfo;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;

import lombok.Builder;

@Builder
public record OrderDto(
		Long id,
		Long memberId,
		String title,
		OrderStatus orderStatus,
		int hopePrice,
		String region,
		LocalDateTime visitDate,
		CakeInfo cakeInfo,
		List<OfferDto> offerDtoList
) {
}
