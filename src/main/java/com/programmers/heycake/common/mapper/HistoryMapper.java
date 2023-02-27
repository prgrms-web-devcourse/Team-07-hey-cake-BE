package com.programmers.heycake.common.mapper;

import static com.programmers.heycake.common.mapper.OrderMapper.*;

import com.programmers.heycake.domain.order.model.dto.request.HistoryFacadeRequest;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistoryMapper {

	public static OrderHistory toOrderHistory(HistoryFacadeRequest historyFacadeRequest) {
		OrderHistory orderHistory = new OrderHistory(historyFacadeRequest.memberId(), historyFacadeRequest.marketId());
		orderHistory.setOrder(toEntity(historyFacadeRequest.orderDto()));
		return orderHistory;
	}
}
