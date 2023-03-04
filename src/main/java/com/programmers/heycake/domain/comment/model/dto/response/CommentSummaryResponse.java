package com.programmers.heycake.domain.comment.model.dto.response;

public record CommentSummaryResponse(Long commentId, String comment, String image, Long memberId) {
}