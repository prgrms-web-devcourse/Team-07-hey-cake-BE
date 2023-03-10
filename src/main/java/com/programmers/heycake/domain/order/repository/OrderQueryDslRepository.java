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
	private static final long MAX_PHOTOS_NUM_PER_ORDER = 3L;

	public List<MyOrderResponse> findAllByMemberIdOrderByVisitDateAsc(
			Long memberId,
			OrderStatus option,
			LocalDateTime cursorDate,
			int pageSize) {

		Map<Long, MyOrderResponse> myOrderResponseMap = jpaQueryFactory
				.select(
						qOrder.id,
						qOrder.title,
						qOrder.orderStatus,
						qOrder.region,
						qOrder.visitDate,
						qOrder.createdAt,
						qOrder.cakeInfo,
						qOrder.hopePrice,
						qImage.imageUrl
				)
				.from(qOrder)
				.leftJoin(qImage)
				.on(
						qOrder.id.eq(qImage.referenceId),
						qImage.imageType.eq(ORDER)
				)
				.where(
						gtVisitDate(cursorDate),
						eqOrderStatus(option),
						qOrder.memberId.eq(memberId)
				).orderBy(qOrder.visitDate.asc(), qImage.id.asc())
				.limit(pageSize * MAX_PHOTOS_NUM_PER_ORDER)
				.transform(
						groupBy(qOrder.id)
								.as(
										Projections.constructor(
												MyOrderResponse.class,
												qOrder.id,
												qOrder.title,
												qOrder.orderStatus,
												qOrder.region,
												qOrder.visitDate,
												qOrder.createdAt,
												qOrder.cakeInfo,
												qOrder.hopePrice,
												qImage.imageUrl
										)
								)
				);

		if (myOrderResponseMap.size() > pageSize) {
			return new ArrayList<>(myOrderResponseMap.values()).subList(0, pageSize);
		}

		return new ArrayList<>(myOrderResponseMap.values());
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
						eqOrderStatus(orderStatus)
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
