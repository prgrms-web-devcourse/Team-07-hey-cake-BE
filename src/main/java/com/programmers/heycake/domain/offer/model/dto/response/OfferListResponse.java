package com.programmers.heycake.domain.offer.model.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

@Builder
public record OfferListResponse(
		Long offerId,
		Long marketId,
		Long enrollmentId,
		String marketName,
		int expectedPrice,
		@JsonFormat(pattern = "yyyy.MM.dd") LocalDate createdDate,
		boolean isPaid,
		String imageUrl,
		String content,
		int commentCount
) {
}