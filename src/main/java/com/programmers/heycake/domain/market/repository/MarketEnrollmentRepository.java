package com.programmers.heycake.domain.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;

public interface MarketEnrollmentRepository extends JpaRepository<MarketEnrollment, Long> {
}
