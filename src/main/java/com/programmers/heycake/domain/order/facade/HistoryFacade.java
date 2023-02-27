package com.programmers.heycake.domain.order.facade;

import org.springframework.stereotype.Component;

import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.service.OfferService;
import com.programmers.heycake.domain.order.model.vo.request.HistoryControllerRequest;
import com.programmers.heycake.domain.order.model.vo.request.HistoryFacadeRequest;
import com.programmers.heycake.domain.order.service.HistoryService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HistoryFacade {
	private final HistoryService historyService;
	private final OfferService offerService;

	public Long createHistory(HistoryControllerRequest historyControllerRequest) {
		//Todo 컨텍스트 memberId와 order의 작성자 같은지 체크

		// DTO로 변경
		Offer offer = offerService.getById(historyControllerRequest.offerId());

		//memberId, orderDto로 변경
		HistoryFacadeRequest historyRequest = new HistoryFacadeRequest(1L, offer.getMarketId(), offer.getOrder());

		return historyService.createHistory(historyRequest);
	}
}
