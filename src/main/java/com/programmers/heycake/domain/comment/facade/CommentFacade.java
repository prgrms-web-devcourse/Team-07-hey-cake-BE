package com.programmers.heycake.domain.comment.facade;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.mapper.CommentMapper;
import com.programmers.heycake.domain.comment.model.dto.response.CommentResponse;
import com.programmers.heycake.domain.comment.model.dto.response.CommentSummaryResponse;
import com.programmers.heycake.domain.comment.service.CommentService;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentFacade {

	private final CommentService commentService;
	private final ImageService imageService;

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
}
