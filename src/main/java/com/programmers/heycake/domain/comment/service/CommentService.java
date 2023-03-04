package com.programmers.heycake.domain.comment.service;

import org.springframework.stereotype.Service;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.common.util.AuthenticationUtil;
import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.comment.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;

	public void deleteComment(Long commentId) {
		Long memberId = AuthenticationUtil.getMemberId();

		Comment comment = getComment(commentId);

		verifyCommentDeleteAuthority(comment, memberId);

		commentRepository.delete(comment);
	}

	private void verifyCommentDeleteAuthority(Comment comment, Long memberId) {
		if (comment.isNotWrittenBy(memberId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
	}

	private Comment getComment(Long commentId) {
		return commentRepository.findById(commentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}
}