package com.programmers.heycake.common.mapper;

import com.programmers.heycake.domain.order.model.dto.request.HistoryCreateFacadeRequest;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistoryMapper {

	public static OrderHistory toOrderHistory(HistoryCreateFacadeRequest historyFacadeRequest) {
		OrderHistory orderHistory = new OrderHistory(historyFacadeRequest.memberId(), historyFacadeRequest.marketId());
		orderHistory.setOrder(historyFacadeRequest.order());
		return orderHistory;
	}
}
