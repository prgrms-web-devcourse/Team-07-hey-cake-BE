package com.programmers.heycake.domain.offer.model.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

public record OfferSaveRequest(
		@NotNull(message = "주문 id를 입력해주세요.")
		@Positive(message = "주문 id 는 양수여야합니다.")
		Long orderId,
		@Positive(message = "희망 가격은 양수여야합니다.")
		int expectedPrice,
		@Length(max = 500, message = "오퍼 내용은 500자까지 입력할 수 있습니다.")
		@NotBlank(message = "오퍼 내용은 공백일 수 없습니다.")
		String content,
		@NotNull(message = "오퍼 이미지는 필수입니다.")
		MultipartFile offerImage
) {
}