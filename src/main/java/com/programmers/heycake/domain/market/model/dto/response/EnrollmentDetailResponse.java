package com.programmers.heycake.domain.market.model.dto.response;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.programmers.heycake.domain.market.model.vo.MarketAddress;

import lombok.Builder;

@Builder
public record EnrollmentDetailResponse(
		String phoneNumber,
		MarketAddress marketAddress,
		@JsonFormat(pattern = "HH:mm") LocalTime openTime,
		@JsonFormat(pattern = "HH:mm") LocalTime endTime,
		String description,
		String marketName,
		String businessNumber,
		String ownerName,
		String marketImage
) {
}
