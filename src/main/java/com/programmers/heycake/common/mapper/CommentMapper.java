package com.programmers.heycake.common.mapper;

import java.util.List;

import com.programmers.heycake.domain.comment.model.dto.response.CommentResponse;
import com.programmers.heycake.domain.comment.model.entity.Comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

	public static CommentResponse toCommentResponse(Comment comment) {
		return new CommentResponse(comment.getId(), comment.getMemberId(), comment.getContent());
	}

	public static List<CommentResponse> toCommentResponseList(List<Comment> commentList) {
		return commentList.stream()
				.map(CommentMapper::toCommentResponse)
				.toList();
	}

}
