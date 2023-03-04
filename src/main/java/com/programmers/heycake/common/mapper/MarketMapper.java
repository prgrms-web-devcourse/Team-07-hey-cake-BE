package com.programmers.heycake.common.mapper;

import static com.programmers.heycake.common.exception.ErrorCode.*;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.market.model.dto.response.MarketDetailNoImageResponse;
import com.programmers.heycake.domain.market.model.dto.response.MarketDetailWithImageResponse;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MarketMapper {

	public static MarketDetailNoImageResponse toMarketDetailNoImageResponse(Market market) {
		return MarketDetailNoImageResponse.builder()
				.phoneNumber(market.getPhoneNumber())
				.address(market.getMarketAddress())
				.openTime(market.getOpenTime())
				.endTime(market.getEndTime())
				.description(market.getDescription())
				.marketName(market.getMarketEnrollment().getMarketName())
				.businessNumber(market.getMarketEnrollment().getBusinessNumber())
				.ownerName(market.getMarketEnrollment().getOwnerName())
				.build();
	}

	public static MarketDetailWithImageResponse toMarketDetailWithImageResponse(MarketDetailNoImageResponse market,
			ImageResponses images) {
		return MarketDetailWithImageResponse.builder()
				.phoneNumber(market.phoneNumber())
				.address(market.address())
				.openTime(market.openTime())
				.endTime(market.endTime())
				.description(market.description())
				.marketName(market.marketName())
				.businessNumber(market.businessNumber())
				.ownerName(market.ownerName())
				.marketImage(
						images.images().stream()
								.findFirst()
								.orElseThrow(() -> {
									throw new BusinessException(ENTITY_NOT_FOUND);
								}).imageUrl())
				.build();
	}

	public static Market toEntity(MarketEnrollment enrollment) {
		return Market.builder()
				.phoneNumber(enrollment.getPhoneNumber())
				.marketAddress(enrollment.getMarketAddress())
				.openTime(enrollment.getOpenTime())
				.endTime(enrollment.getEndTime())
				.description(enrollment.getDescription())
				.build();
	}
}
