package com.programmers.heycake.domain.order.model.vo.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record HistoryControllerRequest(
		@NotNull
		@Positive
		Long orderId,
		@NotNull
		@Positive
		Long offerId
) {
}
