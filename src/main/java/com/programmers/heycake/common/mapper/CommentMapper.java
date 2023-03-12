package com.programmers.heycake.common.mapper;

import java.util.List;

import com.programmers.heycake.domain.comment.model.dto.response.CommentResponse;
import com.programmers.heycake.domain.comment.model.dto.response.CommentSummaryResponse;
import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.member.model.dto.response.MemberResponse;

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

	public static List<CommentResponse> toCommentResponseList(List<Comment> commentList) {
		return commentList.stream()
				.map(CommentMapper::toCommentResponse)
				.toList();
	}

	public static CommentSummaryResponse toCommentSummaryResponse(CommentResponse commentResponse,
			ImageResponses imageResponse, MemberResponse memberResponse) {
		List<String> imageUrls = imageResponse.images().stream()
				.map(ImageResponse::imageUrl)
				.toList();

		String imageUrl = imageUrls
				.stream()
				.findAny()
				.orElse(null);

		return CommentSummaryResponse.builder()
				.commentId(commentResponse.commentId())
				.comment(commentResponse.comment())
				.image(imageUrl)
				.memberId(commentResponse.memberId())
				.nickname(memberResponse.nickname())
				.build();
	}
}
