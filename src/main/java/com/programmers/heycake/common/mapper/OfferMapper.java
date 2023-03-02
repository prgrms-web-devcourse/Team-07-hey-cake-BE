package com.programmers.heycake.common.mapper;

import com.programmers.heycake.domain.offer.model.entity.Offer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OfferMapper {

	public static Offer toEntity(Long marketId, int expectedPrice, String content) {
		return new Offer(marketId, expectedPrice, content);
	}
}