package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.common.utils.JwtUtil.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.member.service.MemberService;
import com.programmers.heycake.domain.offer.facade.OfferFacade;
import com.programmers.heycake.domain.order.model.dto.request.MyOrderRequest;
import com.programmers.heycake.domain.order.model.dto.request.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetResponse;
import com.programmers.heycake.domain.order.service.HistoryService;
import com.programmers.heycake.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderFacade {

	private final HistoryService historyService;
	private final OrderService orderService;
	private final MemberService memberService;
	private static final String ORDER_IMAGE_SUB_PATH = "images/order";
	private final ImageIntegrationService imageIntegrationService;
	private final ImageService imageService;
	private final OfferFacade offerFacade;

	@Transactional
	public void createOrder(OrderCreateRequest orderCreateRequest) {
		Long orderId = orderService.create(orderCreateRequest);
		if (orderCreateRequest.cakeCategory() != null) {
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
	public OrderGetResponse getOrder(Long orderId) {
		return orderService.getOrder(orderId);
	}

	@Transactional
	public void deleteOrder(Long orderId) {
		List<String> imageUrlList = imageService.getImage(orderId, ORDER).imageUrls();
		List<Long> orderOfferIdList = orderService.getOrderOfferIdList(orderId);
		orderService.deleteOrder(orderId, getMemberId());
		imageUrlList.forEach(
				imageUrl ->
						imageIntegrationService.deleteImage(orderId, ORDER, ORDER_IMAGE_SUB_PATH, imageUrl));
		orderOfferIdList.forEach(offerFacade::deleteOfferWithoutAuth);
	}
}

