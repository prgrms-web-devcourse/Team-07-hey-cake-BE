package com.programmers.heycake.domain.market.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;
import com.programmers.heycake.domain.market.model.vo.MarketAddress;

import lombok.Builder;

@Builder
public record EnrollmentForAdminResponse(
		Long enrollmentId,
		String businessNumber,
		MarketAddress address,
		String marketName,
		String phoneNumber,
		String ownerName,
		EnrollmentStatus status,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdAt
) {
}
