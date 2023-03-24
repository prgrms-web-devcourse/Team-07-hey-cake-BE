package com.programmers.heycake.common.mapper;

import static com.programmers.heycake.common.mapper.CommentMapper.*;
import static com.programmers.heycake.common.mapper.OrderMapper.*;

import java.util.List;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.comment.model.dto.response.CommentResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.offer.model.dto.OfferDto;
import com.programmers.heycake.domain.offer.model.dto.response.OfferResponse;
import com.programmers.heycake.domain.offer.model.dto.response.OfferSummaryResponse;
import com.programmers.heycake.domain.offer.model.entity.Offer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OfferMapper {

	public static Offer toEntity(Long marketId, int expectedPrice, String content) {
		return new Offer(marketId, expectedPrice, content);
	}

	public static OfferResponse toOfferResponse(Offer offer) {

		List<CommentResponse> commentResponses = offer.getComments()
				.stream()
				.map(CommentMapper::toCommentResponse)
				.toList();

		return OfferResponse.builder()
				.offerId(offer.getId())
				.marketId(offer.getMarketId())
				.expectedPrice(offer.getExpectedPrice())
				.content(offer.getContent())
				.createdAt(offer.getCreatedAt())
				.commentResponses(commentResponses)
				.build();
	}

	public static OfferSummaryResponse toOfferSummaryResponse(OfferResponse offerResponse, ImageResponses imageResponses,
			Market market, boolean isPaid) {

		String imageUrl = imageResponses.images()
				.stream()
				.findAny()
				.map(ImageResponse::imageUrl)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

		return OfferSummaryResponse.builder()
				.offerId(offerResponse.offerId())
				.marketId(offerResponse.marketId())
				.enrollmentId(market.getMarketEnrollment().getId())
				.marketName(market.getMarketEnrollment().getMarketName())
				.expectedPrice(offerResponse.expectedPrice())
				.imageUrl(imageUrl)
				.content(offerResponse.content())
				.commentCount(offerResponse.commentResponses().size())
				.isPaid(isPaid)
				.createdDate(offerResponse.createdAt().toLocalDate())
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
