package com.programmers.heycake.domain.market.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.member.model.entity.Member;

public interface MarketRepository extends JpaRepository<Market, Long> {

	Optional<Market> findByMember(Member member);

	@Query("SELECT m FROM Market m JOIN FETCH m.marketEnrollment WHERE m.id = :marketId")
	Optional<Market> findFetchWithMarketEnrollmentById(@Param("marketId") Long marketId);
}