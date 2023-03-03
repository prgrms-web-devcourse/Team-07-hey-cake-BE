package com.programmers.heycake.domain.comment.model.dto.response;

public record CommentResponse(Long commentId, Long memberId, String content) {
}
