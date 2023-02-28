package com.programmers.heycake.domain.comment.facade;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.mapper.CommentMapper;
import com.programmers.heycake.common.utils.JwtUtil;
import com.programmers.heycake.domain.comment.model.dto.request.CommentSaveRequest;
import com.programmers.heycake.domain.comment.model.dto.response.CommentResponse;
import com.programmers.heycake.domain.comment.model.dto.response.CommentSummaryResponse;
import com.programmers.heycake.domain.comment.service.CommentService;
import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.image.service.ImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentFacade {

	private static final String COMMENT_SUB_PATH = "image/comment";

	private final CommentService commentService;
	private final ImageService imageService;
	private final ImageIntegrationService imageIntegrationService;

	@Transactional
	public Long saveComment(CommentSaveRequest commentSaveRequest) {
		// TODO : 맴버 검증 넣기
		// Long memberId = JwtUtil.getMemberId();
		Long memberId = 1L;

		Long savedCommentId = commentService.saveComment(commentSaveRequest.content(), commentSaveRequest.offerId(),
				memberId);
		imageIntegrationService.createAndUploadImage(commentSaveRequest.image(), COMMENT_SUB_PATH, savedCommentId,
				ImageType.COMMENT);

		return savedCommentId;
	}

	@Transactional(readOnly = true)
	public List<CommentSummaryResponse> getComments(Long offerId) {
		List<CommentResponse> commentResponses = commentService.getComments(offerId);
		return commentResponses.stream()
				.map(
						commentResponse -> {
							ImageResponse imageResponse = imageService.getImage(commentResponse.commentId(), ImageType.COMMENT);
							return CommentMapper.toCommentSummaryResponse(commentResponse, imageResponse);
						}
				).toList();
	}
}
