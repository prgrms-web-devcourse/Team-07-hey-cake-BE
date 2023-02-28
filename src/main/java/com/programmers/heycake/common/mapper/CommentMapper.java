package com.programmers.heycake.common.mapper;

import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.comment.model.dto.response.CommentResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

	public static Comment toEntity(Long memberId, String content) {
		return new Comment(memberId, content);
	}

	public static CommentResponse toCommentResponse(Comment comment) {
		return new CommentResponse(comment.getId(), comment.getMemberId(), comment.getContent());
	}
}
