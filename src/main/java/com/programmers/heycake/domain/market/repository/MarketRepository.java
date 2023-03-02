package com.programmers.heycake.domain.market.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.programmers.heycake.domain.market.model.entity.Market;

public interface MarketRepository extends JpaRepository<Market, Long> {

	@Query("SELECT m FROM Market m JOIN FETCH m.marketEnrollment WHERE m.id = :marketId")
	Optional<Market> findByIdFetchWithMarketEnrollment(@Param("marketId") Long marketId);
}