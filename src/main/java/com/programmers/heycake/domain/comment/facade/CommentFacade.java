package com.programmers.heycake.domain.comment.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.util.AuthenticationUtil;
import com.programmers.heycake.domain.comment.model.dto.request.CommentSaveRequest;
import com.programmers.heycake.domain.comment.service.CommentService;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentFacade {

	private static final String COMMENT_SUB_PATH = "images/comments";

	private final CommentService commentService;
	private final ImageIntegrationService imageIntegrationService;

	@Transactional
	public Long saveComment(CommentSaveRequest commentSaveRequest) {
		Long memberId = AuthenticationUtil.getMemberId();

		Long savedCommentId = commentService.saveComment(commentSaveRequest.content(), commentSaveRequest.offerId(),
				memberId);
		imageIntegrationService.createAndUploadImage(commentSaveRequest.image(), COMMENT_SUB_PATH, savedCommentId,
				ImageType.COMMENT);

		return savedCommentId;
	}
}