package com.programmers.heycake.domain.order.service;

import static com.programmers.heycake.common.mapper.HistoryMapper.*;
import static com.programmers.heycake.common.mapper.OrderMapper.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.order.model.dto.request.HistoryFacadeRequest;
import com.programmers.heycake.domain.order.model.dto.request.MyOrderRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponse;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;
import com.programmers.heycake.domain.order.repository.HistoryQueryDslRepository;
import com.programmers.heycake.domain.order.repository.HistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {
	private final HistoryRepository historyRepository;
	private final HistoryQueryDslRepository historyQueryDslRepository;

	@Transactional
	public Long createHistory(HistoryFacadeRequest historyFacadeRequest) {
		OrderHistory orderHistory = toOrderHistory(historyFacadeRequest);
		return historyRepository.save(orderHistory).getId();
	}

	@Transactional(readOnly = true)
	public MyOrderResponseList getMyOrderList(MyOrderRequest myOrderRequest, Long marketId) {
		LocalDateTime cursorTime = null;

		if (myOrderRequest.cursorId() != null) {
			Optional<Order> order = historyRepository.findById(myOrderRequest.cursorId())
					.map(OrderHistory::getOrder);
			if (order.isPresent()) {
				cursorTime = order.get().getVisitDate();
			}
		}

		List<MyOrderResponse> orderHistories = historyQueryDslRepository.findAllByMarketIdOrderByVisitDateAsc(
				marketId,
				myOrderRequest.orderStatus(),
				cursorTime,
				myOrderRequest.pageSize()
		);

		Long lastId = orderHistories.isEmpty()
				? Long.MAX_VALUE : orderHistories.get(orderHistories.size() - 1).id();

		return toMyOrderResponseList(orderHistories, lastId);
	}

	@Transactional(readOnly = true)
	public boolean isPaidOffer(Long marketId, Long orderId) {
		return historyRepository.existsByMarketIdAndOrderId(marketId, orderId);
	}
}
