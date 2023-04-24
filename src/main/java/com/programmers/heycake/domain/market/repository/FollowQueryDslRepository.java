package com.programmers.heycake.domain.market.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.programmers.heycake.domain.market.model.dto.request.FollowMarketRequest;
import com.programmers.heycake.domain.market.model.entity.QFollow;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FollowQueryDslRepository {
	private final JPAQueryFactory jpaQueryFactory;

	QFollow qFollow = QFollow.follow;

	public List<Long> getFollowedMarketIds(FollowMarketRequest followMarketRequest, Long memberId) {
		return jpaQueryFactory.select(qFollow.marketId)
				.from(qFollow)
				.where(
						qFollow.memberId.eq(memberId),
						gtId(followMarketRequest.cursorId())
				)
				.limit(followMarketRequest.pageSize())
				.fetch();
	}

	public BooleanExpression gtId(Long id) {
		return id == null ? null : qFollow.id.gt(id);
	}
}
