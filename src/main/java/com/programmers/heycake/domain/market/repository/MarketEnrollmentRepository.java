package com.programmers.heycake.domain.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;

@Repository
public interface MarketEnrollmentRepository extends JpaRepository<MarketEnrollment, Long> {
}
