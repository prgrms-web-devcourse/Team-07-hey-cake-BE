package com.programmers.heycake.domain.comment.model.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CommentSummaryResponse(Long commentId, String comment, String image, Long memberId, String nickname,
																		 LocalDateTime createdAt) {
}