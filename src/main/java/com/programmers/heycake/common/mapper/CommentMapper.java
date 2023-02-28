package com.programmers.heycake.common.mapper;

import java.util.List;

import com.programmers.heycake.domain.comment.model.dto.response.CommentResponse;
import com.programmers.heycake.domain.comment.model.dto.response.CommentSummaryResponse;
import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;

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

	public static CommentSummaryResponse toCommentSummaryResponse(CommentResponse commentResponse,
			ImageResponses imageResponse) {
		List<String> imageUrls = imageResponse.images().stream()
				.map(ImageResponse::imageUrls)
				.toList();

		String imageUrl = imageUrls
				.stream()
				.findAny()
				.orElse(null);

		return new CommentSummaryResponse(commentResponse.commentId(), commentResponse.content(), imageUrl,
				commentResponse.memberId());
	}
}