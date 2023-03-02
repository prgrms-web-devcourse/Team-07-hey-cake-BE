package com.programmers.heycake.domain.market.model.dto;

import java.time.LocalTime;

import com.programmers.heycake.domain.market.model.vo.MarketAddress;

import lombok.Builder;

@Builder
public record MarketResponse(
		String phoneNumber,
		MarketAddress address,
		LocalTime openTime,
		LocalTime endTime,
		String description,
		String marketName,
		String businessNumber,
		String ownerName
) {
}
