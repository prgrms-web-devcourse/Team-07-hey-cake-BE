package com.programmers.heycake.domain.order.model.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record HistoryControllerRequest(
		@NotNull(message = "주문 id 는 필수입니다.")
		@Positive(message = "주문 id 는 양수여야합니다.")
		Long orderId,
		@NotNull(message = "오퍼 id 는 필수입니다.")
		@Positive(message = "오퍼 id 는 양수여야합니다.")
		Long offerId
) {
}
