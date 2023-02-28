package com.programmers.heycake.domain.market.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.programmers.heycake.domain.market.service.MarketService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EnrollmentEventListener {

	private final MarketService marketService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void changeEnrollmentStatus(EnrollmentStatusEvent event) {
		if (event.isApproved()) {
			marketService.enrollMarket(event.getEnrollmentId());
		}
	}
}
