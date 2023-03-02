package com.programmers.heycake.domain.market.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.member.model.entity.Member;

public interface MarketRepository extends JpaRepository<Market, Long> {
	Optional<Market> findByMember(Member member);
}