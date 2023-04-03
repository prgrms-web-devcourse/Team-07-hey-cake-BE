package com.programmers.heycake.domain.facade;

import static com.programmers.heycake.common.mapper.OrderMapper.*;
import static com.programmers.heycake.common.util.AuthenticationUtil.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.market.service.MarketService;
import com.programmers.heycake.domain.member.model.dto.response.OrderDetailResponse;
import com.programmers.heycake.domain.member.service.MemberService;
import com.programmers.heycake.domain.order.model.dto.request.MyOrderRequest;
import com.programmers.heycake.domain.order.model.dto.request.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponse;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.dto.response.OrdersElementResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrdersResponse;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.programmers.heycake.domain.order.service.HistoryService;
import com.programmers.heycake.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderFacade {

	private final OrderService orderService;
	private final MemberService memberService;
	private final HistoryService historyService;
	private final ImageService imageService;
	private final OfferFacade offerFacade;
	private final MarketService marketService;

	private static final String ORDER_IMAGE_SUB_PATH = "images/orders";

	@Transactional
	public Long createOrder(OrderCreateRequest orderCreateRequest) {
		Long orderId = orderService.createOrder(orderCreateRequest);

		orderCreateRequest.cakeImages()
				.forEach(cakeImage ->
						imageService.createAndUploadImage(
								cakeImage,
								ORDER_IMAGE_SUB_PATH,
								orderId,
								ORDER
						));
		return orderId;
	}

	@Transactional(readOnly = true)
	public OrdersResponse getOrders(
			Long cursorId, int pageSize, CakeCategory cakeCategory, OrderStatus orderStatus, String region
	) {
		List<Order> orders = orderService.getOrders(cursorId, pageSize, cakeCategory, orderStatus, region);

		List<OrdersElementResponse> ordersElementResponses =
				orders.stream()
						.map(order -> toOrdersElementResponse(
								order,
								imageService.getImages(order.getId(), ORDER)
						)).toList();

		int size = ordersElementResponses.size();
		long lastCursor = size <= 0 ? 0 : ordersElementResponses.get(size - 1).orderId();
		boolean isLast = size < pageSize;

		return new OrdersResponse(ordersElementResponses, lastCursor, isLast);
	}

	@Transactional(readOnly = true)
	public OrderDetailResponse getOrder(Long orderId) {
		Order order = orderService.getOrderById(orderId);
		return toOrderDetailResponse(order, imageService.getImages(order.getId(), ORDER));
	}

	@Transactional(readOnly = true)
	public MyOrderResponseList getMyOrderList(MyOrderRequest getOrderRequest) {
		Long memberId = getMemberId();
		if (memberService.isMarketById(memberId)) {
			Long marketId = marketService.getMarketIdByMember(memberService.getMemberById(memberId));
			MyOrderResponseList myOrderList =
					historyService.getMyOrderList(getOrderRequest, marketId);

			return new MyOrderResponseList(
					myOrderList.myOrderResponseList()
							.stream()
							.map(myOrder -> new MyOrderResponse(
									myOrder.id(),
									myOrder.title(),
									myOrder.orderStatus(),
									myOrder.region(),
									myOrder.visitTime(),
									myOrder.createdAt(),
									myOrder.cakeInfo(),
									myOrder.hopePrice(),
									myOrder.imageUrl(),
									orderService.offerCount(myOrder.id()))
							).toList(),
					myOrderList.cursorId());
		} else {
			return orderService.getMyOrderList(getOrderRequest, memberId);
		}
	}

	@Transactional
	public void deleteOrder(Long orderId) {
		imageService.deleteImages(orderId, ORDER);

		List<Long> orderOfferIdList = orderService.getOrderOfferIdList(orderId);
		orderOfferIdList.forEach(offerFacade::deleteOfferWithoutAuth);

		orderService.deleteOrder(orderId);
	}
}
