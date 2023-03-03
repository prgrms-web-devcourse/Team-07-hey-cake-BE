package com.programmers.heycake.domain.order.repository;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;
import static com.querydsl.jpa.JPAExpressions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.programmers.heycake.domain.image.model.entity.QImage;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponse;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.QOrder;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderQueryDslRepository {
	private final JPAQueryFactory jpaQueryFactory;
	QOrder qOrder = QOrder.order;
	QImage qImage = QImage.image;

	// public List<Order> findAllByMemberIdOrderByVisitDateAsc(Long memberId, String option, LocalDateTime cursorTime,
	// 		int pageSize) {
	// 	return jpaQueryFactory
	// 			.selectFrom(qOrder)
	// 			.where(
	// 					gtOrderTime(cursorTime),
	// 					orderStatus(option),
	// 					qOrder.memberId.eq(memberId)
	// 			).orderBy(qOrder.visitDate.asc())
	// 			.limit(pageSize)
	// 			.fetch();
	// }

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

	public List<MyOrderResponse> findAllByMemberIdOrderByVisitDateAsc(Long memberId, String option,
			LocalDateTime cursorTime,
			int pageSize) {

		// List<String> imageUrls = jpaQueryFactory.selectFrom(qImage)
		// 		.where(
		// 				qImage.referenceId.eq(qOrder.id),
		// 				qImage.imageType.eq(ORDER)
		// 		).stream()
		// 		.map(Image::getImageUrl)
		// 		.toList();

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
						orderStatus(option),
						qOrder.memberId.eq(memberId)
				).orderBy(qOrder.visitDate.asc())
				.limit(pageSize)
				.fetch();

		// List<Tuple> tuples = jpaQueryFactory
		// 		.select(qOrder, qImage.imageUrl)
		// 		.from(qOrder)
		// 		.leftJoin(qImage)
		// 		.on(qOrder.id.eq(qImage.referenceId),
		// 				qImage.imageType.eq(ORDER))
		// 		.select(qOrder, qImage.imageUrl)
		// 		.from(qOrder)
		// 		.where(
		// 				qOrder.id.eq(memberId),
		// 				gtOrderTime(cursorTime),
		// 				orderStatus(option)
		// 		).orderBy(qOrder.visitDate.asc())
		// 		.limit(pageSize)
		// 		.fetch();
		//
		// return tuples.stream()
		// 		.distinct()
		// 		.map(
		// 				tuple -> MyOrderResponse.builder()
		// 						.id(tuple.get(qOrder.id))
		// 						.title(tuple.get(qOrder.title))
		// 						.orderStatus(tuple.get(qOrder.orderStatus))
		// 						.region(tuple.get(qOrder.region))
		// 						.visitTime(tuple.get(qOrder.visitDate))
		// 						.createdAt(tuple.get(qOrder.createdAt))
		// 						.imageUrl(tuple.get(qImage.imageUrl))
		// 						.build()
		// 		).toList();

		//TODO 리팩토링하기
		// jpaQueryFactory.selectDistinct(qOrder, qImage.imageUrl)
		// 		.from(qOrder)
		// 		.leftJoin(
		// 				select(qImage.referenceId, qImage.imageUrl)
		// 						.from(qImage)
		// 						.where(qImage.imageType.eq(ORDER))
		// 						.groupBy(qImage.referenceId, qImage.imageUrl)
		// 						.limit(1), qImage)
		// 		.on(qOrder.id.eq(qImage.referenceId))
		// 		.fetch();

		// return jpaQueryFactory
		// 		.select(qOrder, qImage.imageUrl)
		// 		.from(qOrder)
		// 		.leftJoin(
		// 				JPAExpressions
		// 						.select(qImage.referenceId, qImage.imageUrl)
		// 						.from(qImage)
		// 						.where(qImage.imageType.eq(ORDER))
		// 						.groupBy(qImage.referenceId, qImage.imageUrl)
		// 						.limit(1),
		// 				on(qOrder.id.eq(qImage.referenceId))
		// 		)
		// 		.where(qOrder.id.eq(1L))
		// 		.fetch();
	}

	private BooleanExpression gtOrderTime(LocalDateTime cursorTime) {
		return cursorTime == null ? null : qOrder.visitDate.gt(cursorTime);
	}

	private BooleanExpression orderStatus(String option) {
		return option == null ? null : qOrder.orderStatus.eq(OrderStatus.valueOf(option));
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
