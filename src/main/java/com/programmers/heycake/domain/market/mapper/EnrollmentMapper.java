package com.programmers.heycake.domain.market.mapper;

import com.programmers.heycake.domain.market.model.dto.EnrollmentRequest;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.model.vo.MarketAddress;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrollmentMapper {

	public static MarketEnrollment toEntity(EnrollmentRequest request) {
		return MarketEnrollment.builder()
				.businessNumber(request.businessNumber())
				.ownerName(request.ownerName())
				.openDate(request.openDate())
				.marketName(request.marketName())
				.phoneNumber(request.phoneNumber())
				.marketAddress(new MarketAddress(
						request.city(),
						request.district(),
						request.detailAddress())
				)
				.openTime(request.openTime())
				.endTime(request.endTime())
				.description(request.description())
				.build();
	}
}