package com.programmers.heycake.domain.market.repository;

import static com.programmers.heycake.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.model.entity.QMarketEnrollment;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EnrollmentQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;
	QMarketEnrollment qMarketEnrollment = QMarketEnrollment.marketEnrollment;

	public List<MarketEnrollment> findAllOrderByCreatedAtDesc(
			Long cursorEnrollmentId,
			Integer pageSize,
			EnrollmentStatus status
	) {

		LocalDateTime cursorCreatedAt = LocalDateTime.now();

		if (cursorEnrollmentId != null) {
			cursorCreatedAt = jpaQueryFactory.selectFrom(qMarketEnrollment)
					.where(getCursorEnrollment(cursorEnrollmentId))
					.stream()
					.findFirst()
					.orElseThrow(() -> {
						throw new BusinessException(ENTITY_NOT_FOUND);
					}).getCreatedAt();
		}

		return jpaQueryFactory
				.selectFrom(qMarketEnrollment)
				.where(
						gtCursorCreatedAt(cursorCreatedAt),
						enrollmentStatus(status)
				).orderBy(qMarketEnrollment.createdAt.desc())
				.limit(pageSize)
				.fetch();
	}

	private BooleanExpression getCursorEnrollment(Long cursorEnrollmentId) {
		return cursorEnrollmentId == null ? null : qMarketEnrollment.id.eq(cursorEnrollmentId);
	}

	private BooleanExpression gtCursorCreatedAt(LocalDateTime cursorCreatedAt) {
		return cursorCreatedAt == null ? null : qMarketEnrollment.createdAt.lt(cursorCreatedAt);
	}

	private BooleanExpression enrollmentStatus(EnrollmentStatus status) {
		return status == null ? null : qMarketEnrollment.enrollmentStatus.eq(status);
	}
}
