package com.programmers.heycake.domain.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.programmers.heycake.domain.order.model.entity.OrderHistory;
import com.programmers.heycake.domain.order.model.entity.QOrderHistory;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HistoryQueryDslRepository {
	private final JPAQueryFactory jpaQueryFactory;

	QOrderHistory qOrderHistory = QOrderHistory.orderHistory;

	public List<OrderHistory> findAllByMarketIdOrderByVisitDateAsc(
			Long memberId,
			String option,
			LocalDateTime cursorTime,
			int pageSize) {
		return jpaQueryFactory
				.selectFrom(qOrderHistory)
				.where(
						gtOrderTime(cursorTime),
						eqOrderStatus(option),
						qOrderHistory.marketId.eq(memberId)
				).orderBy(qOrderHistory.order.visitDate.asc())
				.limit(pageSize)
				.fetch();

	}

	private BooleanExpression gtOrderTime(LocalDateTime cursorTime) {
		return cursorTime == null ? null : qOrderHistory.order.visitDate.gt(cursorTime);
	}

	private BooleanExpression eqOrderStatus(String option) {
		return option == null ? null : qOrderHistory.order.orderStatus.eq(OrderStatus.valueOf(option));
	}
}
