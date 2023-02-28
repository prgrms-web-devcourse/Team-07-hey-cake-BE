package com.programmers.heycake.domain.comment.model.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.web.multipart.MultipartFile;

public record CommentSaveRequest(
		@Positive
		@NotNull
		Long offerId,
		@NotBlank
		String content,
		MultipartFile image) {
}
