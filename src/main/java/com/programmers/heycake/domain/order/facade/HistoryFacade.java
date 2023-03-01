package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.common.utils.JwtUtil.*;

import org.springframework.stereotype.Component;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.offer.model.dto.OfferDto;
import com.programmers.heycake.domain.offer.service.OfferService;
import com.programmers.heycake.domain.order.model.dto.request.HistoryControllerRequest;
import com.programmers.heycake.domain.order.model.dto.request.HistoryFacadeRequest;
import com.programmers.heycake.domain.order.service.HistoryService;
import com.programmers.heycake.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HistoryFacade {
	private final HistoryService historyService;
	private final OfferService offerService;
	private final OrderService orderService;

	public Long createHistory(HistoryControllerRequest historyControllerRequest) {
		Long memberId = getMemberId();
		if (!orderService.isAuthor(historyControllerRequest.orderId(), memberId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}

		OfferDto offer = offerService.getOfferById(historyControllerRequest.offerId());
		HistoryFacadeRequest historyRequest = new HistoryFacadeRequest(memberId, offer.marketId(), offer.orderDto());

		return historyService.createHistory(historyRequest);
	}
}
