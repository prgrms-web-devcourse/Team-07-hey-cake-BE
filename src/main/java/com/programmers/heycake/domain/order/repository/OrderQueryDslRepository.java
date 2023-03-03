package com.programmers.heycake.domain.order.repository;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.programmers.heycake.domain.image.model.entity.QImage;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponse;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.QOrder;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderQueryDslRepository {
	private final JPAQueryFactory jpaQueryFactory;
	QOrder qOrder = QOrder.order;
	QImage qImage = QImage.image;

	public List<MyOrderResponse> findAllByMemberIdOrderByVisitDateAsc(Long memberId, String option,
			LocalDateTime cursorTime,
			int pageSize) {

		return jpaQueryFactory
				.select(
						Projections.constructor(
								MyOrderResponse.class,
								qOrder.id,
								qOrder.title,
								qOrder.orderStatus,
								qOrder.region,
								qOrder.visitDate,
								qOrder.createdAt,
								qImage.imageUrl
						)
				)
				.from(qOrder)
				.leftJoin(qImage)
				.on(
						qOrder.id.eq(qImage.referenceId).and(qImage.imageType.eq(ORDER))
				).limit(1)
				.where(
						gtOrderTime(cursorTime),
						eqOrderStatus(option),
						qOrder.memberId.eq(memberId)
				).orderBy(qOrder.visitDate.asc())
				.limit(pageSize)
				.fetch();
	}

	private BooleanExpression gtOrderTime(LocalDateTime cursorTime) {
		return cursorTime == null ? null : qOrder.visitDate.gt(cursorTime);
	}

	private BooleanExpression eqOrderStatus(String option) {
		return option == null ? null : qOrder.orderStatus.eq(OrderStatus.valueOf(option));
	}

	public List<Order> findAllByRegionAndCategoryOrderByCreatedAtAsc(
			Long cursorId,
			int pageSize,
			CakeCategory cakeCategory,
			String region
	) {
		return jpaQueryFactory
				.selectFrom(qOrder)
				.where(
						ltOrderId(cursorId),
						eqRegion(region),
						eqCakeCategory(cakeCategory)
				)
				.limit(pageSize)
				.orderBy(qOrder.createdAt.desc())
				.fetch();
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
