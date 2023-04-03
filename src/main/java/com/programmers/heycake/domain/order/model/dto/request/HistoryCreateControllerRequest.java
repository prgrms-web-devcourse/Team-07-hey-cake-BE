package com.programmers.heycake.domain.order.model.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record HistoryCreateControllerRequest(
		@NotNull @Positive Long orderId,
		@NotNull @Positive Long offerId,
		@NotNull Boolean isPaid
) {
}
