package com.programmers.heycake.domain.comment.model.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(Long commentId, Long memberId, String comment, LocalDateTime createdAt) {
}
