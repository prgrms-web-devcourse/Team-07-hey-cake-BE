package com.programmers.heycake.domain.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.QOrder;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderCustomRepositoryImpl implements OrderCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;
	QOrder qOrder = QOrder.order;

	@Override
	public List<Order> findAllByMemberIdOrderByVisitDateAsc(Long memberId, String option, LocalDateTime cursorTime,
			int pageSize) {
		return jpaQueryFactory
				.selectFrom(qOrder)
				.where(
						gtOrderTime(cursorTime),
						orderStatus(option),
						qOrder.memberId.eq(memberId)
				).orderBy(qOrder.visitDate.asc())
				.limit(pageSize)
				.fetch();
	}

	private BooleanExpression gtOrderTime(LocalDateTime cursorTime) {
		return cursorTime == null ? null : qOrder.visitDate.gt(cursorTime);
	}

	private BooleanExpression orderStatus(String option) {
		return option == null ? null : qOrder.orderStatus.eq(OrderStatus.valueOf(option));
	}
}
