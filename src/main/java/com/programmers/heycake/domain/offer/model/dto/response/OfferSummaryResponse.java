package com.programmers.heycake.domain.offer.model.dto.response;

import lombok.Builder;

@Builder
public record OfferSummaryResponse(Long offerId, Long marketId, String marketName, int expectedPrice, String imageUrl,
																	 String content, int commentCount) {
}