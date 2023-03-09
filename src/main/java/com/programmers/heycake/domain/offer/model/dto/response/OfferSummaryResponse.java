package com.programmers.heycake.domain.offer.model.dto.response;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record OfferSummaryResponse(Long offerId, Long marketId, Long enrollmentId, String marketName, int expectedPrice,
																	 LocalDate createdDate, boolean isPaid, String imageUrl, String content,
																	 int commentCount) {
}