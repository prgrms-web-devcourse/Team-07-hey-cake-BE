package com.programmers.heycake.domain.offer.model.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

public record OfferCreateRequest(
		@NotNull @Positive Long orderId,
		@Positive int expectedPrice,
		@Length(max = 500) @NotBlank String content,
		@NotNull MultipartFile offerImage
) {
}