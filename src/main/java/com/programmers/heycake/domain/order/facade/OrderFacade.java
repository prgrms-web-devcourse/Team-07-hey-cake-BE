package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.order.model.dto.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.request.GetOrderRequest;
import com.programmers.heycake.domain.order.model.dto.response.GetOrderResponseList;
import com.programmers.heycake.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderFacade {
	private final OrderService orderService;
	// private final MemberService memberService; //TODO 추가요망
	// private final HistoryService historyService; //TODO 추가요망
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
	public GetOrderResponseList getOrderList(GetOrderRequest getOrderRequest) {
		//TODO exist 쿼리 or authority 로 분기
		//if(memberservice.isMarket(memberId)) {
		// 업주일때
		// }
		//if(!memberservice.isMarket(memberId)) {
		//회원일때
		//Todo memberId 찾아오기
		GetOrderResponseList orderList = orderService.getOrderList(getOrderRequest, 1L);
		return orderList;
	}
}

