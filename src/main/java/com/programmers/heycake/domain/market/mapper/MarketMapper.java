package com.programmers.heycake.domain.market.mapper;

import static com.programmers.heycake.common.exception.ErrorCode.*;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.market.model.dto.MarketControllerResponse;
import com.programmers.heycake.domain.market.model.dto.MarketResponse;
import com.programmers.heycake.domain.market.model.entity.Market;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MarketMapper {

	public static MarketResponse toResponse(Market market) {
		return MarketResponse.builder()
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

	public static MarketControllerResponse toControllerResponse(MarketResponse market, ImageResponses images) {
		return MarketControllerResponse.builder()
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
}
