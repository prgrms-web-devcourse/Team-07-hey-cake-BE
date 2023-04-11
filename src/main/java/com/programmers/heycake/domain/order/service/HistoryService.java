package com.programmers.heycake.domain.order.service;

import static com.programmers.heycake.domain.order.mapper.HistoryMapper.*;
import static com.programmers.heycake.domain.order.mapper.OrderMapper.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.common.util.AuthenticationUtil;
import com.programmers.heycake.domain.order.model.dto.request.HistoryCreateFacadeRequest;
import com.programmers.heycake.domain.order.model.dto.request.MyOrdersRequest;
import com.programmers.heycake.domain.order.model.dto.request.UpdateSugarScoreRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponse;
import com.programmers.heycake.domain.order.model.dto.response.MyOrdersResponse;
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
	public Long createHistory(HistoryCreateFacadeRequest historyCreateFacadeRequest) {
		OrderHistory orderHistory = toOrderHistory(historyCreateFacadeRequest);
		return historyRepository.save(orderHistory).getId();
	}

	@Transactional(readOnly = true)
	public MyOrdersResponse getMyOrders(MyOrdersRequest myOrdersRequest, Long marketId) {
		LocalDateTime cursorTime = null;

		if (myOrdersRequest.cursorId() != null) {
			Optional<Order> order = historyRepository.findById(myOrdersRequest.cursorId())
					.map(OrderHistory::getOrder);
			if (order.isPresent()) {
				cursorTime = order.get().getVisitDate();
			}
		}

		List<MyOrderResponse> orderHistories = historyQueryDslRepository.findAllByMarketIdOrderByVisitDateAsc(
				marketId,
				myOrdersRequest.orderStatus(),
				cursorTime,
				myOrdersRequest.pageSize()
		);

		Long lastId = orderHistories.isEmpty() ? 0L : orderHistories.get(orderHistories.size() - 1).id();

		return toMyOrdersResponse(orderHistories, lastId);
	}

	@Transactional
	public OrderHistory getOrderHistory(Long orderHistoryId) {
		return historyRepository.findById(orderHistoryId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public boolean isPaidOffer(Long marketId, Long orderId) {
		return historyRepository.existsByMarketIdAndOrderId(marketId, orderId);
	}

	@Transactional
	public void updateSugarScore(UpdateSugarScoreRequest updateSugarScoreRequest) {
		OrderHistory orderHistory = getOrderHistory(updateSugarScoreRequest.orderHistoryId());
		isOrderWriter(orderHistory);
		orderHistory.updateSugarScore(updateSugarScoreRequest.sugarScore());
	}

	private void isOrderWriter(OrderHistory orderHistory) {
		if (!orderHistory.getMemberId().equals(AuthenticationUtil.getMemberId())) {
			throw new AccessDeniedException("잘못된 접근입니다.");
		}
	}
}
