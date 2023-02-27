package com.programmers.heycake.domain.order.facade;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.order.model.dto.request.GetOrderRequest;
import com.programmers.heycake.domain.order.model.dto.response.GetOrderResponseList;
import com.programmers.heycake.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderFacade {

	// private final MemberService memberService;
	// private final HistoryService historyService;
	private final OrderService orderService;

	@Transactional
	public GetOrderResponseList getOrderList(GetOrderRequest getOrderRequest) {
		//TODO exist쿼리 or authority로 분기
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
