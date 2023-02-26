package com.programmers.heycake.common.mapper;

import com.programmers.heycake.domain.order.model.entity.OrderHistory;
import com.programmers.heycake.domain.order.model.vo.request.HistoryRequest;

public class HistoryMapper {

	public static OrderHistory toOrderHistory(HistoryRequest historyRequest) {
		OrderHistory orderHistory = new OrderHistory(historyRequest.memberId(), historyRequest.marketId());
		orderHistory.setOrder(historyRequest.order());
		return orderHistory;
	}
}
