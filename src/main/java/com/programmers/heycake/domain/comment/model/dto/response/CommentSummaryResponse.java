package com.programmers.heycake.domain.comment.model.dto.response;

import lombok.Builder;

@Builder
public record CommentSummaryResponse(Long commentId, String comment, String image, Long memberId, String nickname) {
}