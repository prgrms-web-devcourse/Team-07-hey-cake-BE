package com.programmers.heycake.domain.market.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.programmers.heycake.domain.market.model.entity.Market;

public interface MarketRepository extends JpaRepository<Market, Long> {

	@Query("SELECT m FROM Market m JOIN FETCH m.marketEnrollment")
	Optional<Market> findByIdFetchWithMarketEnrollment(Long marketId);
}
