package com.programmers.heycake.domain.offer.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.programmers.heycake.domain.comment.model.dto.response.CommentResponse;

import lombok.Builder;

@Builder
public record OfferResponse(Long offerId, Long marketId, int expectedPrice, String content,
														LocalDateTime createdAt, List<CommentResponse> commentResponses) {
}