package com.programmers.heycake.domain.order.repository;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;
import static com.querydsl.core.group.GroupBy.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.programmers.heycake.domain.image.model.entity.QImage;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponse;
import com.programmers.heycake.domain.order.model.entity.QOrderHistory;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HistoryQueryDslRepository {
	private final JPAQueryFactory jpaQueryFactory;

	QOrderHistory qOrderHistory = QOrderHistory.orderHistory;
	QImage qImage = QImage.image;

	public List<MyOrderResponse> findAllByMarketIdOrderByVisitDateAsc(
			Long marketId,
			String option,
			LocalDateTime cursorDate,
			int pageSize) {

		Map<Long, MyOrderResponse> myOrderResponseMap = jpaQueryFactory
				.select(
						qOrderHistory.order.id,
						qOrderHistory.order.title,
						qOrderHistory.order.orderStatus,
						qOrderHistory.order.region,
						qOrderHistory.order.visitDate,
						qOrderHistory.order.createdAt,
						qImage.imageUrl
				)
				.from(qOrderHistory)
				.leftJoin(qImage)
				.on(
						qOrderHistory.order.id.eq(qImage.referenceId),
						qImage.imageType.eq(ORDER)
				)
				.where(
						gtVisitDate(cursorDate),
						eqOrderStatus(option),
						qOrderHistory.marketId.eq(marketId)
				).orderBy(qOrderHistory.order.visitDate.asc())
				.limit(pageSize)
				.transform(
						groupBy(qOrderHistory.id)
								.as(
										Projections.constructor(
												MyOrderResponse.class,
												qOrderHistory.order.id,
												qOrderHistory.order.title,
												qOrderHistory.order.orderStatus,
												qOrderHistory.order.region,
												qOrderHistory.order.visitDate,
												qOrderHistory.order.createdAt,
												qImage.imageUrl
										)
								)
				);

		return new ArrayList<>(myOrderResponseMap.values());
	}

	private BooleanExpression gtVisitDate(LocalDateTime cursorTime) {
		return cursorTime == null ? null : qOrderHistory.order.visitDate.gt(cursorTime);
	}

	private BooleanExpression eqOrderStatus(String option) {
		return option == null ? null : qOrderHistory.order.orderStatus.eq(OrderStatus.valueOf(option));
	}
}
