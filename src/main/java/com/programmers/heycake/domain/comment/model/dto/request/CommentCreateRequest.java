package com.programmers.heycake.domain.comment.model.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

public record CommentCreateRequest(
		@NotNull
		@Positive
		Long offerId,
		@Length(max = 500)
		@NotBlank
		String content,
		MultipartFile image
) {
	public boolean existsImage() {
		return this.image != null;
	}
}