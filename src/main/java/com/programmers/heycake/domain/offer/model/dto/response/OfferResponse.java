package com.programmers.heycake.domain.offer.model.dto.response;

import java.util.List;

import com.programmers.heycake.domain.comment.model.entity.response.CommentResponse;

import lombok.Builder;

@Builder
public record OfferResponse(Long offerId, Long marketId, int expectedPrice, String content,
														List<CommentResponse> commentResponses) {

}
