package com.programmers.heycake.domain.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.market.model.entity.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	void deleteByMemberIdAndMarketId(Long memberId, Long marketId);

	boolean existsByMemberIdAndMarketId(Long memberId, Long marketId);

	int countByMarketId(Long marketId);
}
