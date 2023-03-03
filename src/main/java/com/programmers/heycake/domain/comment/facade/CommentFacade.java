package com.programmers.heycake.domain.comment.facade;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public void deleteComment(Long commentId) {
		commentService.deleteComment(commentId);

		List<ImageResponse> commentImageResponse = imageService.getImages(commentId, ImageType.COMMENT).images();
		if (!commentImageResponse.isEmpty()) {
			commentImageResponse
					.forEach(imageResponse ->
							imageIntegrationService.deleteImages(commentId, ImageType.COMMENT, COMMENT_SUB_PATH))
			;
		}
	}
}
