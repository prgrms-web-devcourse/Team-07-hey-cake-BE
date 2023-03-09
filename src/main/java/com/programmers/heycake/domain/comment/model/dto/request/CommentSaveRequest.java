package com.programmers.heycake.domain.comment.model.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

public record CommentSaveRequest(
		@NotNull(message = "오퍼 id는 필수입니다.")
		@Positive(message = "오퍼 id는 양수여야합니다.")
		Long offerId,
		@Length(max = 500, message = "댓글 내용은 500자까지 입력할 수 있습니다.")
		@NotBlank(message = "댓글 내용은 공백일 수 없습니다.")
		String content,
		MultipartFile image) {
}