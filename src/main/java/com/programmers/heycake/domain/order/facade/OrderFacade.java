package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.order.model.dto.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.request.MyOrderRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.service.HistoryService;
import com.programmers.heycake.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderFacade {

	private final HistoryService historyService;
	private final OrderService orderService;
	// private final MemberService memberService; //TODO 추가요망
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
		//TODO exist쿼리 or authority로 분기
		//if(memberservice.isMarket(memberId)) {
		// 업주일때
		//TODO marketID 찾아오기
		MyOrderResponseList myOrderList = historyService.getMyOrderList(getOrderRequest, 1L);
		// }
		//if(!memberservice.isMarket(memberId)) {
		//회원일때
		//Todo memberId 찾아오기
		// MyOrderResponseList myOrderList = orderService.getMyOrderList(getOrderRequest, 1L);

		return myOrderList;
	}
}
