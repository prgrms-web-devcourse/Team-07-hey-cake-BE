package com.programmers.heycake.common.mapper;

import java.util.List;

import com.programmers.heycake.domain.comment.model.dto.response.ChildCommentsResponse;
import com.programmers.heycake.domain.comment.model.dto.response.CommentResponse;
import com.programmers.heycake.domain.comment.model.dto.response.CommentsResponse;
import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.member.model.entity.Member;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

	public static Comment toEntity(Long memberId, String content, Long parentCommentId) {
		return new Comment(memberId, content, parentCommentId);
	}

	public static CommentResponse toCommentResponse(Comment comment) {
		return new CommentResponse(comment.getId(), comment.getMemberId(), comment.getContent(), comment.getCreatedAt());
	}

	public static List<CommentResponse> toCommentResponseList(List<Comment> commentList) {
		return commentList.stream()
				.map(CommentMapper::toCommentResponse)
				.toList();
	}

	public static CommentsResponse toCommentsResponse(
			Comment comment,
			Member member,
			ImageResponses imageResponse,
			int childCommentCount
	) {
		List<String> imageUrls = imageResponse.images().stream()
				.map(ImageResponse::imageUrl)
				.toList();

		String imageUrl = imageUrls
				.stream()
				.findAny()
				.orElse(null);

		return CommentsResponse.builder()
				.commentId(comment.getId())
				.comment(comment.getContent())
				.memberId(comment.getMemberId())
				.createdAt(comment.getCreatedAt())
				.nickname(member.getNickname())
				.image(imageUrl)
				.childCommentCount(childCommentCount)
				.isDeleted(comment.isDeleted())
				.build();
	}

	public static ChildCommentsResponse toChildCommentResponse(
			Comment comment,
			Member member,
			ImageResponses imageResponses
	) {
		List<String> imageUrls = imageResponses.images().stream()
				.map(ImageResponse::imageUrl)
				.toList();

		String imageUrl = imageUrls
				.stream()
				.findAny()
				.orElse(null);

		return ChildCommentsResponse.builder()
				.commentId(comment.getId())
				.comment(comment.getContent())
				.memberId(comment.getMemberId())
				.createdAt(comment.getCreatedAt())
				.nickname(member.getNickname())
				.image(imageUrl)
				.isDeleted(comment.isDeleted())
				.build();
	}
}
