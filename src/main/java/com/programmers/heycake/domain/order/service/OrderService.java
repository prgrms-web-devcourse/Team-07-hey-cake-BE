package com.programmers.heycake.domain.order.service;

import static com.programmers.heycake.common.mapper.OrderMapper.*;
import static com.programmers.heycake.common.utils.AuthenticationUtil.*;
import static com.programmers.heycake.domain.order.model.vo.OrderStatus.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.common.mapper.OrderMapper;
import com.programmers.heycake.domain.image.service.ImageService;
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
import com.programmers.heycake.domain.order.repository.OrderQueryDslRepository;
import com.programmers.heycake.domain.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final OrderQueryDslRepository orderQueryDslRepository;
	private final ImageService imageService;

	@Transactional
	public Long create(OrderCreateRequest orderCreateRequest) {
		Long memberId = getMemberId();
		CakeInfo cakeInfo = CakeInfo.builder()
				.cakeCategory(orderCreateRequest.cakeCategory())
				.cakeSize(orderCreateRequest.cakeSize())
				.cakeHeight(orderCreateRequest.cakeHeight())
				.breadFlavor(orderCreateRequest.breadFlavor())
				.creamFlavor(orderCreateRequest.creamFlavor())
				.requirements(orderCreateRequest.requirements())
				.build();

		Order savedOrder = orderRepository.save(
				Order.builder()
						.cakeInfo(cakeInfo)
						.hopePrice(orderCreateRequest.hopePrice())
						.memberId(memberId)
						.orderStatus(NEW)
						.visitDate(orderCreateRequest.visitTime())
						.title(orderCreateRequest.title())
						.region(orderCreateRequest.region())
						.build()
		);
		return savedOrder.getId();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public MyOrderResponseList getMyOrderList(MyOrderRequest getOrderRequest, Long memberId) {
		// List<Order> orderList = orderQueryDslRepository.findAllByMemberIdOrderByVisitDateAsc(
		List<MyOrderResponse> orderList = orderQueryDslRepository.findAllByMemberIdOrderByVisitDateAsc(
				memberId,
				getOrderRequest.orderStatus(),
				getOrderRequest.cursorTime(),
				getOrderRequest.pageSize()
		);

		//TODO 삭제
		// List<MyOrderResponse> orderDtoWithImages = orderList.stream()
		// 		.map(o ->
		// 				MyOrderResponse.builder()
		// 						.id(o.getId())
		// 						.title(o.getTitle())
		// 						.orderStatus(o.getOrderStatus())
		// 						.hopePrice(o.getHopePrice())
		// 						.region(o.getRegion())
		// 						.visitDate(o.getVisitDate())
		// 						.image(String.valueOf(
		// 								imageService.getImages(o.getId(), ORDER).images().stream().findFirst())
		// 						).build()
		// 		).toList();
		// return new MyOrderResponseList(orderDtoWithImages, lastTime);

		LocalDateTime lastTime =
				orderList.isEmpty() ? LocalDateTime.MAX : orderList.get(orderList.size() - 1).visitTime();

		return toMyOrderResponseListForMember(orderList, lastTime);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public OrderGetDetailServiceResponse getOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(EntityNotFoundException::new);
		return toOrderGetServiceDetailResponse(order);
	}

	public List<OrderGetServiceSimpleResponse> getOrders(
			Long cursorId, int pageSize, CakeCategory cakeCategory, String region
	) {
		return orderQueryDslRepository
				.findAllByRegionAndCategoryOrderByCreatedAtAsc(cursorId, pageSize, cakeCategory, region)
				.stream()
				.map(OrderMapper::toOrderGetServiceSimpleResponse)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public boolean isAuthor(Long orderId, Long memberId) {
		Order order = getEntity(orderId);
		return Objects.equals(order.getMemberId(), memberId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteOrder(Long orderId, Long memberId) {
		if (!Objects.equals(getOrder(orderId).memberId(), memberId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
		if (!isNew(orderId)) {
			throw new BusinessException(ErrorCode.DELETE_ERROR);
		}
		orderRepository.deleteById(orderId);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Long> getOrderOfferIdList(Long orderId) {
		return getEntity(orderId)
				.getOffers()
				.stream()
				.map(Offer::getId)
				.toList();
	}

	private boolean isNew(Long orderId) {
		return getEntity(orderId).getOrderStatus().equals(NEW);
	}

	private Order getEntity(Long orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}
}
