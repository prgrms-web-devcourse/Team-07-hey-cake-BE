package com.programmers.heycake.domain.offer.model.dto;

import java.util.List;

import com.programmers.heycake.domain.comment.model.dto.response.CommentResponse;
import com.programmers.heycake.domain.order.model.dto.OrderDto;

import lombok.Builder;

@Builder
public record OfferDto(
		Long id,
		Long marketId,
		int expectedPrice,
		String content,
		OrderDto orderDto,
		List<CommentResponse> commentResponseList
) {
}
