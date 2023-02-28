package com.programmers.heycake.domain.order.service;

import static com.programmers.heycake.common.mapper.OrderMapper.*;
import static com.programmers.heycake.domain.order.model.vo.OrderStatus.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.mapper.OrderMapper;
import com.programmers.heycake.domain.offer.service.OfferService;
import com.programmers.heycake.domain.order.model.dto.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.request.GetOrderRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetResponse;
import com.programmers.heycake.domain.order.model.entity.CakeInfo;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.repository.OrderCustomRepository;
import com.programmers.heycake.domain.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final OrderCustomRepository orderCustomRepository;

	private final OfferService offerService;

	@Transactional
	public Long create(OrderCreateRequest orderCreateRequest) {
		CakeInfo cakeInfo = CakeInfo.builder()
				.cakeCategory(orderCreateRequest.cakeCategory())
				.cakeSize(orderCreateRequest.cakeSize())
				.cakeHeight(orderCreateRequest.cakeHeight())
				.breadFlavor(orderCreateRequest.breadFlavor())
				.creamFlavor(orderCreateRequest.creamFlavor())
				.requirements(orderCreateRequest.requirements())
				.build();

		// try {
		Order savedOrder = orderRepository.save(
				Order.builder()
						.cakeInfo(cakeInfo)
						.hopePrice(orderCreateRequest.hopePrice())
						.memberId(1L) // TODO memberId 넣어주기
						.orderStatus(NEW)
						.visitDate(orderCreateRequest.visitTime())
						.title(orderCreateRequest.title())
						.region(orderCreateRequest.region())
						.build()
		);
		// } catch (Exception e) {
		// 	System.out.println("##### #######");
		// 	e.printStackTrace();
		// }
		return savedOrder.getId();
	}

	@Transactional(readOnly = true)
	public MyOrderResponseList getMyOrderList(GetOrderRequest getOrderRequest, Long memberId) {
		List<Order> orderList = orderCustomRepository.findAllByMemberIdOrderByVisitDateAsc(
				memberId,
				getOrderRequest.orderStatus(),
				getOrderRequest.cursorTime(),
				getOrderRequest.pageSize()
		);

		LocalDateTime lastTime =
				orderList.size() == 0 ? LocalDateTime.MAX : orderList.get(orderList.size() - 1).getVisitDate();

		return toGetMyOrderResponseList(orderList, lastTime);
	}

	@Transactional
	public OrderGetResponse getOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(EntityNotFoundException::new);
		return OrderMapper.toGetOrderResponse(order);
	}
}
