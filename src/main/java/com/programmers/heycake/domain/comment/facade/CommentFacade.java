package com.programmers.heycake.domain.comment.facade;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.mapper.CommentMapper;
import com.programmers.heycake.domain.comment.model.dto.request.CommentSaveRequest;
import com.programmers.heycake.domain.comment.model.dto.response.CommentResponse;
import com.programmers.heycake.domain.comment.model.dto.response.CommentSummaryResponse;
import com.programmers.heycake.domain.comment.service.CommentService;
import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
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

		Long savedCommentId = commentService.saveComment(commentSaveRequest.content(), commentSaveRequest.offerId());
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
							ImageResponses imageResponse = imageService.getImages(commentResponse.commentId(), ImageType.COMMENT);
							return CommentMapper.toCommentSummaryResponse(commentResponse, imageResponse);
						}
				).toList();
	}

	@Transactional
	public void deleteComment(Long commentId) {
		commentService.deleteComment(commentId);

		List<ImageResponse> commentImageResponse = imageService.getImages(commentId, ImageType.COMMENT).images();
		if (commentImageResponse.size() > 0) {
			commentImageResponse
					.forEach(imageResponse ->
							imageIntegrationService.deleteImage(commentId, ImageType.COMMENT, COMMENT_SUB_PATH,
									imageResponse.savedFilename()))
			;
		}
	}
}
