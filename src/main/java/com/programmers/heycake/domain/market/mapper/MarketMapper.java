package com.programmers.heycake.domain.market.mapper;

import static com.programmers.heycake.common.exception.ErrorCode.*;

import java.util.List;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.market.model.dto.response.FollowMarketsResponse;
import com.programmers.heycake.domain.market.model.dto.response.MarketDetailResponse;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MarketMapper {

	public static MarketDetailResponse toMarketDetailResponse(Market market, ImageResponses images) {
		return MarketDetailResponse.builder()
				.phoneNumber(market.getPhoneNumber())
				.address(market.getMarketAddress())
				.openTime(market.getOpenTime())
				.endTime(market.getEndTime())
				.description(market.getDescription())
				.marketName(market.getMarketEnrollment().getMarketName())
				.businessNumber(market.getMarketEnrollment().getBusinessNumber())
				.ownerName(market.getMarketEnrollment().getOwnerName())
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

	public static FollowMarketsResponse toFollowMarketsResponse(List<MarketDetailResponse> myFollowMarkets,
			Long lastCursorId) {
		return new FollowMarketsResponse(myFollowMarkets, lastCursorId);
	}
}
