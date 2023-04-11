package com.programmers.heycake.domain.market.model.dto.response;

import java.util.List;

public record FollowMarketsResponse(
		List<MarketDetailResponse> myFollowMarkets,
		Long lastCursorId
) {
}
