package com.programmers.heycake.domain.market.model.dto.request;

public record FollowMarketRequest(
		Long cursorId,
		int pageSize
) {
}
