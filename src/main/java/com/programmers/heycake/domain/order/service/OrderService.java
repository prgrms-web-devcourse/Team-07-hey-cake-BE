package com.programmers.heycake.domain.order.service;

import static com.programmers.heycake.common.mapper.OrderMapper.*;
import static com.programmers.heycake.common.util.AuthenticationUtil.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.order.model.dto.request.MyOrdersRequest;
import com.programmers.heycake.domain.order.model.dto.request.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponse;
import com.programmers.heycake.domain.order.model.dto.response.MyOrdersResponse;
import com.programmers.heycake.domain.order.model.entity.CakeInfo;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.programmers.heycake.domain.order.repository.OrderQueryDslRepository;
import com.programmers.heycake.domain.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final OrderQueryDslRepository orderQueryDslRepository;

	@Transactional
	public void updateOrderState(Long orderId, OrderStatus orderStatus) {
		identifyAuthor(orderId);
		isNew(orderId);
		getOrderById(orderId).upDateOrderStatus(orderStatus);
	}

	@Transactional(readOnly = true)
	public MyOrdersResponse getMyOrders(MyOrdersRequest myOrdersRequest, Long memberId) {
		LocalDateTime cursorTime = null;
		if (myOrdersRequest.cursorId() != null) {
			cursorTime = orderRepository.findById(myOrdersRequest.cursorId())
					.map(Order::getVisitDate)
					.orElse(null);
		}

		List<MyOrderResponse> orders = orderQueryDslRepository.findAllByMemberIdOrderByVisitDateAsc(
				memberId,
				myOrdersRequest.orderStatus(),
				cursorTime,
				myOrdersRequest.pageSize()
		);

		Long lastId = orders.isEmpty() ? 0L : orders.get(orders.size() - 1).id();

		return toMyOrdersResponse(orders, lastId);
	}

	@Transactional
	public Long createOrder(OrderCreateRequest orderCreateRequest) {
		CakeInfo cakeInfo = CakeInfo.builder()
				.cakeCategory(orderCreateRequest.cakeCategory())
				.cakeSize(orderCreateRequest.cakeSize())
				.cakeHeight(orderCreateRequest.cakeHeight())
				.breadFlavor(orderCreateRequest.breadFlavor())
				.creamFlavor(orderCreateRequest.creamFlavor())
				.requirements(orderCreateRequest.requirements())
				.build();

		Order savedOrder = orderRepository.save(
				toEntity(orderCreateRequest, cakeInfo)
		);
		return savedOrder.getId();
	}

	public List<Order> getOrders(
			Long cursorId, int pageSize, CakeCategory cakeCategory, OrderStatus orderStatus, String region
	) {
		return orderQueryDslRepository
				.findAllByRegionAndCategoryOrderByCreatedAtAsc(
						cursorId, pageSize, cakeCategory, orderStatus, region
				)
				.stream()
				.toList();
	}

	@Transactional
	public Order getOrderById(Long orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public List<Long> getOrdersOfferId(Long orderId) {
		return getOrderById(orderId)
				.getOffers()
				.stream()
				.map(Offer::getId)
				.toList();
	}

	@Transactional
	public void deleteOrder(Long orderId) {
		identifyAuthor(orderId);
		isNew(orderId);
		orderRepository.deleteById(orderId);
	}

	@Transactional(readOnly = true)
	public void hasOffer(Long orderId, Long offerId) {
		List<Offer> offers = getOrderById(orderId).getOffers();
		if (offers.stream().noneMatch(offer -> offer.isMatch(offerId))) {
			throw new BusinessException(ErrorCode.BAD_REQUEST);
		}
	}

	@Transactional(readOnly = true)
	public int offerCount(Long orderId) {
		return getOrderById(orderId).getOffers().size();
	}

	public boolean existsById(Long orderId) {
		return orderRepository.existsById(orderId);
	}

	private void identifyAuthor(Long orderId) {
		if (!getOrderById(orderId).identifyAuthor(getMemberId())) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
	}

	private void isNew(Long orderId) {
		if (getOrderById(orderId).isExpired()) {
			throw new BusinessException(ErrorCode.ORDER_EXPIRED);
		}
	}
}
