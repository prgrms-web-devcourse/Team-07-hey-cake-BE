package com.programmers.heycake.domain.order.service;

import static com.programmers.heycake.common.mapper.OrderMapper.*;
import static com.programmers.heycake.common.util.AuthenticationUtil.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.common.mapper.OrderMapper;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.order.model.dto.request.MyOrderRequest;
import com.programmers.heycake.domain.order.model.dto.request.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponse;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetDetailServiceResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetServiceSimpleResponse;
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
		getOrder(orderId).upDateOrderStatus(orderStatus);
	}

	@Transactional(readOnly = true)
	public MyOrderResponseList getMyOrderList(MyOrderRequest myOrderRequest, Long memberId) {
		LocalDateTime cursorTime = null;
		if (myOrderRequest.cursorId() != null) {
			cursorTime = orderRepository.findById(myOrderRequest.cursorId())
					.map(Order::getVisitDate)
					.orElse(null);
		}

		List<MyOrderResponse> orderList = orderQueryDslRepository.findAllByMemberIdOrderByVisitDateAsc(
				memberId,
				myOrderRequest.orderStatus(),
				cursorTime,
				myOrderRequest.pageSize()
		);

		Long lastId = orderList.isEmpty() ? 0L : orderList.get(orderList.size() - 1).id();

		return toMyOrderResponseList(orderList, lastId);
	}

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

		Order savedOrder = orderRepository.save(
				toEntity(orderCreateRequest, cakeInfo)
		);
		return savedOrder.getId();
	}

	public List<OrderGetServiceSimpleResponse> getOrders(
			Long cursorId, int pageSize, CakeCategory cakeCategory, OrderStatus orderStatus, String region
	) {
		return orderQueryDslRepository
				.findAllByRegionAndCategoryOrderByCreatedAtAsc(cursorId, pageSize, cakeCategory, orderStatus, region)
				.stream()
				.map(OrderMapper::toOrderGetServiceSimpleResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public OrderGetDetailServiceResponse getOrderDetail(Long orderId) {
		return OrderMapper.toOrderGetDetailServiceResponse(getOrder(orderId));
	}

	@Transactional(readOnly = true)
	public List<Long> getOrderOfferIdList(Long orderId) {
		return getOrder(orderId)
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
		List<Offer> offerList = getOrder(orderId).getOffers();
		if (offerList.stream().noneMatch(offer -> offer.isMatch(offerId))) {
			throw new BusinessException(ErrorCode.BAD_REQUEST);
		}
	}

	public Order getOrder(Long orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

	private void identifyAuthor(Long orderId) {
		if (!getOrder(orderId).identifyAuthor(getMemberId())) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
	}

	private void isNew(Long orderId) {
		if (getOrder(orderId).isClosed()) {
			throw new BusinessException(ErrorCode.ORDER_CLOSED);
		}
	}
}
