package com.programmers.heycake.domain.order.service;

import static com.programmers.heycake.common.mapper.OrderMapper.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.order.dto.response.OrderGetDetailServiceResponse;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;

	@Transactional(readOnly = true)
	public OrderGetDetailServiceResponse getOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
		return toOrderGetServiceDetailResponse(order);
	}
}
