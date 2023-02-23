package com.programmers.heycake.domain.market.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record MarketEnrollmentRequest(
		@NotNull Long memberId,
		@NotBlank String businessNumber,
		@NotBlank String city,
		@NotBlank String district,
		@NotBlank String detailAddress,
		@NotBlank String marketName,
		@NotBlank String ownerName,
		@NotBlank String phoneNumber
) {
}
