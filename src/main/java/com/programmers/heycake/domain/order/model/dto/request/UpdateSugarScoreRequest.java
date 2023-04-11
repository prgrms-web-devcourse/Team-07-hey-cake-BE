package com.programmers.heycake.domain.order.model.dto.request;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

public record UpdateSugarScoreRequest(
		@NotNull
		Long orderHistoryId,

		@NotNull
		@Range(min = 0, max = 100)
		Integer sugarScore
) {
}
