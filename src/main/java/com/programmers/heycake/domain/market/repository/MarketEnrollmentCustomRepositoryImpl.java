package com.programmers.heycake.domain.market.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.model.entity.QMarketEnrollment;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MarketEnrollmentCustomRepositoryImpl implements MarketEnrollmentCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;
	QMarketEnrollment qMarketEnrollment = QMarketEnrollment.marketEnrollment;

	@Override
	public List<MarketEnrollment> findAllOrderByCreatedAtDesc(
			Long cursorEnrollmentId,
			Integer pageSize,
			EnrollmentStatus status
	) {
		return jpaQueryFactory
				.selectFrom(qMarketEnrollment)
				.where(
						getCursorEnrollment(cursorEnrollmentId),
						enrollmentStatus(status)
				).orderBy(qMarketEnrollment.createdAt.desc())
				.limit(pageSize)
				.fetch();
	}

	private BooleanExpression getCursorEnrollment(Long cursorEnrollmentId) {
		return cursorEnrollmentId == null ? null : qMarketEnrollment.id.eq(cursorEnrollmentId);
	}

	private BooleanExpression enrollmentStatus(EnrollmentStatus status) {
		return status == null ? null : qMarketEnrollment.enrollmentStatus.eq(status);
	}
}
