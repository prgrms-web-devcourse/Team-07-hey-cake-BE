package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.common.utils.JwtUtil.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.mapper.OrderMapper;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.member.service.MemberService;
import com.programmers.heycake.domain.order.model.dto.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.request.MyOrderRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetSimpleServiceResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrdersGetResponse;
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
	private static final String SUB_PATH = "image/order";
	private final ImageIntegrationService imageIntegrationService;

	private final ImageService imageService;

	@Transactional
	public void createOrder(OrderCreateRequest orderCreateRequest) {
		Long orderId = orderService.create(orderCreateRequest);
		if (orderCreateRequest.cakeCategory() != null) {
			for (int i = 0; i < orderCreateRequest.cakeImages().size(); i++) {
				imageIntegrationService.createAndUploadImage(
						orderCreateRequest.cakeImages().get(i),
						SUB_PATH,
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
			return historyService.getMyOrderList(getOrderRequest, 1L);
		} else {
			return orderService.getMyOrderList(getOrderRequest, 1L);
		}
	}

	@Transactional
	public OrderGetResponse getOrder(Long orderId) {
		return orderService.getOrder(orderId);
	}

	@Transactional
	public OrdersGetResponse getOrders(
			Long cursorId, int pageSize, CakeCategory cakeCategory, String region
	) {
		List<OrderGetSimpleServiceResponse> orders =
				orderService.getOrders(cursorId, pageSize, cakeCategory, region);

		orders.stream()
				.map(
						orderSimpleGetServiceResponse ->
								OrderMapper.toOrderSimpleGetResponse(
										orderSimpleGetServiceResponse,
										imageService.getImage(orderSimpleGetServiceResponse.orderId(), ORDER)
								))
				.collect(Collectors.toList())
		;
		return new OrdersGetResponse(orders);
	}

	@Transactional
	public void deleteOrder(Long orderId) {
		List<String> imageUrlList = imageService.getImage(orderId, ImageType.ORDER).imageUrls();
		orderService.deleteOrder(orderId);
		imageUrlList.forEach(
				imageUrl ->
						imageIntegrationService.deleteImage(orderId, ORDER, SUB_PATH, imageUrl));
		//관련 스레드 삭제
		// offerFacade
	}
}
