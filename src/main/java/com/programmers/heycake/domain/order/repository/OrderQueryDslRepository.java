package com.programmers.heycake.domain.order.repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.QOrder;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;
	QOrder qOrder = QOrder.order;

	public List<Order> findAllByMemberIdOrderByVisitDateAsc(
			Long memberId,
			OrderStatus option,
			LocalDateTime cursorDate,
			int pageSize) {

		List<Order> myOrders = new java.util.ArrayList<>(jpaQueryFactory
				.select(
						qOrder
				)
				.from(qOrder)
				.where(
						gtVisitDate(cursorDate),
						eqOrderStatus(option),
						qOrder.memberId.eq(memberId)
				).limit(pageSize)
				.fetchAll()
				.stream()
				.toList());

		myOrders.sort(Comparator.comparing(Order::getVisitDate));

		return myOrders;
	}

	public List<Order> findAllByRegionAndCategoryOrderByCreatedAtAsc(
			Long cursorId,
			int pageSize,
			CakeCategory cakeCategory,
			OrderStatus orderStatus,
			String region
	) {
		return jpaQueryFactory
				.selectFrom(qOrder)
				.where(
						ltOrderId(cursorId),
						eqRegion(region),
						eqCakeCategory(cakeCategory),
						eqOrderStatus(orderStatus),
						gtVisitDate(LocalDateTime.now())
				)
				.limit(pageSize)
				.orderBy(qOrder.createdAt.desc())
				.fetch();
	}

	private BooleanExpression gtVisitDate(LocalDateTime cursorTime) {
		return cursorTime == null ? null : qOrder.visitDate.gt(cursorTime);
	}

	private BooleanExpression eqOrderStatus(OrderStatus option) {
		return option == null ? null : qOrder.orderStatus.eq(option);
	}

	private BooleanExpression eqRegion(String region) {
		return region == null ? null : qOrder.region.eq(region);
	}

	private BooleanExpression eqCakeCategory(CakeCategory category) {
		return category == null ? null : qOrder.cakeInfo.cakeCategory.eq(category);
	}

	private BooleanExpression ltOrderId(Long cursorId) {
		return cursorId == null ? null : qOrder.id.lt(cursorId);
	}
}
