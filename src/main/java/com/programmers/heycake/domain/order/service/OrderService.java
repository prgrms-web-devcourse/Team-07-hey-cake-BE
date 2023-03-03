package com.programmers.heycake.domain.order.service;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.programmers.heycake.domain.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;

	@Transactional
	public void updateOrderState(Long orderId, OrderStatus orderStatus) {
		isAuthor(orderId);
		getEntity(orderId).upDateOrderStatus(orderStatus);
	}

	private Order getEntity(Long orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

	private void isAuthor(Long orderId) {
		if (getEntity(orderId).isAuthor(getMemberId())) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
	}
}
