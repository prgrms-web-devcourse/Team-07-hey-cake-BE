package com.programmers.heycake.domain.market.mapper;

import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentRequest;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.model.vo.MarketAddress;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarketEnrollmentMapper {

	public static MarketEnrollment toEntity(MarketEnrollmentRequest marketEnrollmentRequest) {
		return MarketEnrollment.builder()
				.businessNumber(marketEnrollmentRequest.businessNumber())
				.marketAddress(new MarketAddress(
						marketEnrollmentRequest.city(),
						marketEnrollmentRequest.district(),
						marketEnrollmentRequest.detailAddress())
				)
				.marketName(marketEnrollmentRequest.marketName())
				.ownerName(marketEnrollmentRequest.ownerName())
				.phoneNumber(marketEnrollmentRequest.phoneNumber())
				.build();
	}
}
