package com.programmers.heycake.common.mapper;

import static com.programmers.heycake.common.mapper.OrderMapper.*;

import com.programmers.heycake.domain.order.model.dto.request.HistoryFacadeRequest;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistoryMapper {

	public static OrderHistory toOrderHistory(HistoryFacadeRequest historyRequest) {
		OrderHistory orderHistory = new OrderHistory(historyRequest.memberId(), historyRequest.marketId());
		orderHistory.setOrder(toEntity(historyRequest.orderDto()));
		return orderHistory;
	}
}
