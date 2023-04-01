package com.programmers.heycake.domain.facade;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.offer.model.dto.OfferDto;
import com.programmers.heycake.domain.offer.service.OfferService;
import com.programmers.heycake.domain.order.model.dto.request.HistoryCreateControllerRequest;
import com.programmers.heycake.domain.order.model.dto.request.HistoryCreateFacadeRequest;
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
	public Long createHistory(HistoryCreateControllerRequest historyCreateRequest) {
		orderService.hasOffer(historyCreateRequest.orderId(), historyCreateRequest.offerId());

		OrderStatus orderStatus = checkPayment(historyCreateRequest.isPaid());
		orderService.updateOrderState(historyCreateRequest.orderId(), orderStatus);

		OfferDto offerDto = offerService.getOfferById(historyCreateRequest.offerId());
		Order order = orderService.getOrderById(offerDto.orderDto().id());
		HistoryCreateFacadeRequest historyFacadeRequest =
				new HistoryCreateFacadeRequest(getMemberId(), offerDto.marketId(), order);

		return historyService.createHistory(historyFacadeRequest);
	}

	private OrderStatus checkPayment(Boolean isPaid) {
		if (isPaid) {
			return OrderStatus.PAID;
		}
		return OrderStatus.RESERVED;
	}
}
