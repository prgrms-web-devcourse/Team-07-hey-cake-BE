package com.programmers.heycake.domain.market.model.dto.response;

import java.util.List;

public record FollowedMarketsResponse(
		List<MarketResponse> followedMarkets,
		Long cursorId
) {
}
