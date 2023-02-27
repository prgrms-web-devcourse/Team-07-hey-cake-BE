package com.programmers.heycake.domain.market.mapper;

import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.market.model.dto.MarketControllerResponse;
import com.programmers.heycake.domain.market.model.dto.MarketResponse;
import com.programmers.heycake.domain.market.model.entity.Market;

public class MarketMapper {

	public static MarketResponse toControllerResponse(Market market) {
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
				.marketImage(images.images().get(0).imageUrls())
				.build();
	}
}
