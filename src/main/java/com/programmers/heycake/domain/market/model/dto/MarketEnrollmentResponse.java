package com.programmers.heycake.domain.market.model.dto;

import java.time.LocalTime;

import com.programmers.heycake.domain.market.model.vo.MarketAddress;

import lombok.Builder;

@Builder
public record MarketEnrollmentResponse(
		String phoneNumber,
		MarketAddress marketAddress,
		LocalTime openTime,
		LocalTime endTime,
		String description,
		String marketName,
		String businessNumber,
		String ownerName
) {
}