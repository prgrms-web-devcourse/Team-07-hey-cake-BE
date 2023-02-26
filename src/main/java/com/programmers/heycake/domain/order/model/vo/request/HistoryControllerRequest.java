package com.programmers.heycake.domain.order.model.vo.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public record HistoryControllerRequest(
		@NotBlank
		@Positive
		Long orderId,
		@NotBlank
		@Positive
		Long offerId
) {
}
