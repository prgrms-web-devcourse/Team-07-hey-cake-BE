package com.programmers.heycake.domain.facade;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.offer.model.dto.OfferDto;
import com.programmers.heycake.domain.offer.service.OfferService;
import com.programmers.heycake.domain.order.model.dto.request.HistoryCreateControllerRequest;
import com.programmers.heycake.domain.order.model.dto.request.HistoryCreateFacadeRequest;
import com.programmers.heycake.domain.order.model.dto.request.UpdateSugarScoreRequest;
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
	public Long createHistory(HistoryCreateControllerRequest historyCreateControllerRequest) {
		orderService.hasOffer(historyCreateControllerRequest.orderId(), historyCreateControllerRequest.offerId());

		OrderStatus orderStatus = checkPayment(historyCreateControllerRequest.isPaid());
		orderService.updateOrderState(historyCreateControllerRequest.orderId(), orderStatus);

		OfferDto offerDto = offerService.getOfferById(historyCreateControllerRequest.offerId());
		Order order = orderService.getOrderById(offerDto.orderDto().id());
		HistoryCreateFacadeRequest historyCreateFacadeRequest =
				new HistoryCreateFacadeRequest(getMemberId(), offerDto.marketId(), order);

		return historyService.createHistory(historyCreateFacadeRequest);
	}

	@Transactional
	public void updateSugarScore(UpdateSugarScoreRequest updateSugarScoreRequest) {
		historyService.updateSugarScore(updateSugarScoreRequest);
	}

	private OrderStatus checkPayment(Boolean isPaid) {
		if (isPaid) {
			return OrderStatus.PAID;
		}
		return OrderStatus.RESERVED;
	}
}
