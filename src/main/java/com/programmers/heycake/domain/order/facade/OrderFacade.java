package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.common.mapper.OrderMapper.*;
import static com.programmers.heycake.common.utils.AuthenticationUtil.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.mapper.OrderMapper;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.member.service.MemberService;
import com.programmers.heycake.domain.offer.facade.OfferFacade;
import com.programmers.heycake.domain.order.model.dto.request.MyOrderRequest;
import com.programmers.heycake.domain.order.model.dto.request.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetDetailResponse;
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

	private final HistoryService historyService;
	private final OrderService orderService;
	private final MemberService memberService;

	private static final String ORDER_IMAGE_SUB_PATH = "images/orders";

	private final ImageIntegrationService imageIntegrationService;
	private final ImageService imageService;
	private final OfferFacade offerFacade;

	@Transactional
	public void createOrder(OrderCreateRequest orderCreateRequest) {
		Long orderId = orderService.create(orderCreateRequest);
		if (orderCreateRequest.cakeImages() != null) {
			for (int i = 0; i < orderCreateRequest.cakeImages().size(); i++) {
				imageIntegrationService.createAndUploadImage(
						orderCreateRequest.cakeImages().get(i),
						ORDER_IMAGE_SUB_PATH,
						orderId,
						ORDER
				);
			}
		}
	}

	@Transactional
	public MyOrderResponseList getMyOrderList(MyOrderRequest getOrderRequest) {
		Long memberId = getMemberId();
		if (memberService.isMarketById(memberId)) {
			return historyService.getMyOrderList(getOrderRequest, memberId);
		} else {
			return orderService.getMyOrderList(getOrderRequest, memberId);
		}
	}

	@Transactional
	public OrderGetDetailResponse getOrder(Long orderId) {
		OrderGetDetailServiceResponse orderGetDetailServiceResponse = orderService.getOrder(orderId);
		ImageResponses imageResponses = imageService.getImages(orderGetDetailServiceResponse.orderId(), ORDER);
		return OrderMapper.toOrderGetDetailResponse(orderGetDetailServiceResponse, imageResponses);
	}

	@Transactional
	public OrderGetSimpleResponses getOrders(
			Long cursorId, int pageSize, CakeCategory cakeCategory, String region
	) {
		List<OrderGetServiceSimpleResponse> orderGetSimpleServiceResponses =
				orderService.getOrders(cursorId, pageSize, cakeCategory, region);

		List<OrderGetSimpleResponse> orderGetSimpleResponses =
				orderGetSimpleServiceResponses
						.stream()
						.map(orderSimpleGetServiceResponse ->
								toOrderGetSimpleResponse(
										orderSimpleGetServiceResponse,
										imageService.getImages(orderSimpleGetServiceResponse.orderId(), ORDER))
						)
						.toList();

		int size = orderGetSimpleResponses.size();
		long lastCursor = 0;

		for (OrderGetSimpleResponse orderGetSimpleResponse : orderGetSimpleResponses) {
			lastCursor = orderGetSimpleResponse.orderId();
		}

		if (size < pageSize) {
			return new OrderGetSimpleResponses(orderGetSimpleResponses, lastCursor, true);
		}
		return new OrderGetSimpleResponses(orderGetSimpleResponses, lastCursor, false);
	}

	@Transactional
	public void deleteOrder(Long orderId) {
		ImageResponses images = imageService.getImages(orderId, ORDER);
		images.images()
				.forEach(
						image -> imageIntegrationService.deleteImage(
								orderId, ImageType.ORDER, ORDER_IMAGE_SUB_PATH, image.savedFilename())
				);

		List<Long> orderOfferIdList = orderService.getOrderOfferIdList(orderId);
		orderOfferIdList.forEach(offerFacade::deleteOfferWithoutAuth);

		orderService.deleteOrder(orderId, getMemberId());
	}
}
