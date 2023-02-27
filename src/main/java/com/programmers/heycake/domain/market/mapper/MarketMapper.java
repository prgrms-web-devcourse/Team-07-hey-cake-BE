package com.programmers.heycake.domain.market.mapper;

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.market.model.dto.MarketControllerResponse;
import com.programmers.heycake.domain.market.model.dto.MarketResponse;
import com.programmers.heycake.domain.market.model.entity.Market;

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

	public static MarketControllerResponse toResponse(MarketResponse market, ImageResponse image) {
		return MarketControllerResponse.builder()
				.phoneNumber(market.phoneNumber())
				.address(market.address())
				.openTime(market.openTime())
				.endTime(market.endTime())
				.description(market.description())
				.marketName(market.marketName())
				.businessNumber(market.businessNumber())
				.ownerName(market.ownerName())
				.marketImage(image.imageUrl())
				.build();
	}
}
