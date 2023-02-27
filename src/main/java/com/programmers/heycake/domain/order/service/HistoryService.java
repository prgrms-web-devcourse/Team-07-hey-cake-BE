package com.programmers.heycake.domain.order.service;

import static com.programmers.heycake.common.mapper.HistoryMapper.*;
import static com.programmers.heycake.common.mapper.OrderMapper.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.order.model.dto.request.MyOrderRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;
import com.programmers.heycake.domain.order.model.vo.request.HistoryFacadeRequest;
import com.programmers.heycake.domain.order.repository.HistoryCustomRepository;
import com.programmers.heycake.domain.order.repository.HistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {
	private final HistoryRepository historyRepository;
	private final HistoryCustomRepository historyCustomRepository;

	public Long createHistory(HistoryFacadeRequest historyRequest) {
		OrderHistory orderHistory = toOrderHistory(historyRequest);
		return historyRepository.save(orderHistory).getId();
	}

	@Transactional(readOnly = true)
	public MyOrderResponseList getMyOrderList(MyOrderRequest getOrderRequest, Long marketId) {
		List<OrderHistory> orderHistories = historyCustomRepository.findAllByMarketIdOrderByVisitDateAsc(
				marketId,
				getOrderRequest.orderStatus(),
				getOrderRequest.cursorTime(),
				getOrderRequest.pageSize()
		);

		LocalDateTime lastTime =
				orderHistories.size() == 0 ? LocalDateTime.MAX :
						orderHistories.get(orderHistories.size() - 1).getOrder().getVisitDate();

		return toGetOrderResponseListForMarket(orderHistories, lastTime);
	}
}
