package com.programmers.heycake.domain.order.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record MyOrderResponseList(
		List<MyOrderResponse> myOrderResponseList,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		LocalDateTime lastCursorDate
) {
}
