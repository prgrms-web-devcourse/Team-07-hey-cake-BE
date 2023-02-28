package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.common.utils.JwtUtil.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.member.service.MemberService;
import com.programmers.heycake.domain.order.model.dto.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.request.MyOrderRequest;
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
	private static final String SUB_PATH = "image";
	private final ImageIntegrationService imageIntegrationService;

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
			return historyService.getMyOrderList(getOrderRequest, memberId);
		} else {
			return orderService.getMyOrderList(getOrderRequest, memberId);
		}

	}

	@Transactional
	public OrderGetResponse getOrder(Long orderId) {
		return orderService.getOrder(orderId);
	}
}

