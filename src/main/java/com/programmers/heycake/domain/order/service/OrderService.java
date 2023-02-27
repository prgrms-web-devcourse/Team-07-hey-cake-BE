package com.programmers.heycake.domain.order.service;

import static com.programmers.heycake.common.mapper.OrderMapper.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.order.model.dto.request.GetOrderRequest;
import com.programmers.heycake.domain.order.model.dto.response.GetOrderResponseList;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.repository.OrderCustomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderCustomRepository orderCustomRepository;

	@Transactional(readOnly = true)
	public GetOrderResponseList getOrderList(GetOrderRequest getOrderRequest, Long memberId) {
		List<Order> orderList = orderCustomRepository.findAllByMemberIdOrderByVisitDateAsc(
				memberId,
				getOrderRequest.orderStatus(),
				getOrderRequest.cursorTime(),
				getOrderRequest.pageSize()
		);

		LocalDateTime lastTime =
				orderList.size() == 0 ? LocalDateTime.MAX : orderList.get(orderList.size() - 1).getVisitDate();

		return toGetOrderResponseList(orderList, lastTime);
	}
}
