package com.programmers.heycake.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.member.model.entity.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	void deleteByMemberIdAndMarketId(Long memberId, Long marketId);

	boolean existsByMemberIdAndMarketId(Long memberId, Long marketId);
}
