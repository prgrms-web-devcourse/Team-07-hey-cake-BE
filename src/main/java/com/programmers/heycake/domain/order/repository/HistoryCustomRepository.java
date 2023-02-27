package com.programmers.heycake.domain.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.programmers.heycake.domain.order.model.entity.OrderHistory;

public interface HistoryCustomRepository {
	List<OrderHistory> findAllByMarketIdOrderByVisitDateAsc(Long memberId, String option, LocalDateTime cursorTime,
			int pageSize);
}
