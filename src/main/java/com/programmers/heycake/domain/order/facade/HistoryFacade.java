package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.offer.model.dto.OfferDto;
import com.programmers.heycake.domain.offer.service.OfferService;
import com.programmers.heycake.domain.order.model.dto.request.HistoryControllerRequest;
import com.programmers.heycake.domain.order.model.dto.request.HistoryFacadeRequest;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.programmers.heycake.domain.order.service.HistoryService;
import com.programmers.heycake.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HistoryFacade {
	private final HistoryService historyService;
	private final OfferService offerService;
	private final OrderService orderService;

	@Transactional
	public Long createHistory(HistoryControllerRequest historyControllerRequest) {
		orderService.updateOrderState(historyControllerRequest.orderId(), OrderStatus.RESERVED);
		OfferDto offerDto = offerService.getOfferById(historyControllerRequest.offerId());
		Order order = orderService.getOrder(offerDto.orderDto().id());
		HistoryFacadeRequest historyFacadeRequest = new HistoryFacadeRequest(getMemberId(), offerDto.marketId(), order);

		return historyService.createHistory(historyFacadeRequest);
	}
}
