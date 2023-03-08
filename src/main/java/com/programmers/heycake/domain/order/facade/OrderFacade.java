package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.common.mapper.OrderMapper.*;
import static com.programmers.heycake.common.util.AuthenticationUtil.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.member.model.dto.response.OrderGetDetailResponse;
import com.programmers.heycake.domain.member.service.MemberService;
import com.programmers.heycake.domain.offer.facade.OfferFacade;
import com.programmers.heycake.domain.order.model.dto.request.MyOrderRequest;
import com.programmers.heycake.domain.order.model.dto.request.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetDetailServiceResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetServiceSimpleResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetSimpleResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetSimpleResponses;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
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
	private final ImageIntegrationService imageIntegrationService;

	private static final String ORDER_IMAGE_SUB_PATH = "images/orders";

	@Transactional
	public Long createOrder(OrderCreateRequest orderCreateRequest) {
		Long orderId = orderService.create(orderCreateRequest);

		orderCreateRequest.cakeImages()
				.forEach(
						cakeImage ->
								imageIntegrationService.createAndUploadImage(
										cakeImage,
										ORDER_IMAGE_SUB_PATH,
										orderId,
										ORDER
								));
		return orderId;
	}

	@Transactional(readOnly = true)
	public OrderGetSimpleResponses getOrders(
			Long cursorId, int pageSize, CakeCategory cakeCategory, String region, String orderStatus
	) {
		List<OrderGetServiceSimpleResponse> orderGetSimpleServiceResponses =
				orderService.getOrders(cursorId, pageSize, cakeCategory, region, orderStatus);

		List<OrderGetSimpleResponse> orderGetSimpleResponseList =
				orderGetSimpleServiceResponses
						.stream()
						.map(orderSimpleGetServiceResponse ->
								toOrderGetSimpleResponse(
										orderSimpleGetServiceResponse,
										imageService.getImages(orderSimpleGetServiceResponse.orderId(), ORDER))
						)
						.toList();

		int size = orderGetSimpleResponseList.size();
		long lastCursor = size <= 0 ? 0 : orderGetSimpleResponseList.get(size - 1).orderId();
		boolean isLast = size < pageSize;

		return new OrderGetSimpleResponses(orderGetSimpleResponseList, lastCursor, isLast);
	}

	@Transactional(readOnly = true)
	public OrderGetDetailResponse getOrder(Long orderId) {
		OrderGetDetailServiceResponse orderGetDetailServiceResponse = orderService.getOrderDetail(orderId);
		ImageResponses imageResponses = imageService.getImages(orderGetDetailServiceResponse.orderId(), ORDER);
		return toOrderGetDetailResponse(orderGetDetailServiceResponse, imageResponses);
	}

	@Transactional(readOnly = true)
	public MyOrderResponseList getMyOrderList(MyOrderRequest getOrderRequest) {
		Long memberId = getMemberId();
		if (memberService.isMarketById(memberId)) {
			return historyService.getMyOrderList(getOrderRequest, memberId);
		} else {
			return orderService.getMyOrderList(getOrderRequest, memberId);
		}
	}

	@Transactional
	public void deleteOrder(Long orderId) {
		imageIntegrationService.deleteImages(orderId, ORDER, ORDER_IMAGE_SUB_PATH);

		List<Long> orderOfferIdList = orderService.getOrderOfferIdList(orderId);
		orderOfferIdList.forEach(offerFacade::deleteOfferWithoutAuth);

		orderService.deleteOrder(orderId);
	}
}
