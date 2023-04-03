package com.programmers.heycake.domain.offer.mapper;

import static com.programmers.heycake.common.mapper.CommentMapper.*;
import static com.programmers.heycake.domain.order.mapper.OrderMapper.*;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.offer.model.dto.OfferDto;
import com.programmers.heycake.domain.offer.model.dto.response.OffersResponse;
import com.programmers.heycake.domain.offer.model.entity.Offer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OfferMapper {

	public static Offer toEntity(Long marketId, int expectedPrice, String content) {
		return new Offer(marketId, expectedPrice, content);
	}

	public static OffersResponse toOffersResponse(
			Offer offer,
			Market market,
			ImageResponses imageResponses,
			boolean isPaid,
			int numberOfCommentsInOffer
	) {
		String imageUrl = imageResponses.images()
				.stream()
				.findAny()
				.map(ImageResponse::imageUrl)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

		return OffersResponse.builder()
				.offerId(offer.getId())
				.createdDate(offer.getCreatedAt().toLocalDate())
				.expectedPrice(offer.getExpectedPrice())
				.marketId(market.getId())
				.enrollmentId(market.getMarketEnrollment().getId())
				.marketName(market.getMarketEnrollment().getMarketName())
				.imageUrl(imageUrl)
				.content(offer.getContent())
				.commentCount(numberOfCommentsInOffer)
				.isPaid(isPaid)
				.build();
	}

	public static OfferDto toOfferDto(Offer offer) {
		return OfferDto.builder()
				.id(offer.getId())
				.marketId(offer.getMarketId())
				.expectedPrice(offer.getExpectedPrice())
				.content(offer.getContent())
				.orderDto(toOrderDto(offer.getOrder()))
				.commentResponseList(toCommentResponseList(offer.getComments()))
				.build();
	}
}
