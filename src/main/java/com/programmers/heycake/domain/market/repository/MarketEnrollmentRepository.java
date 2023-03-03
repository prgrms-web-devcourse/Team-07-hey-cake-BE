package com.programmers.heycake.domain.market.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;

public interface MarketEnrollmentRepository extends JpaRepository<MarketEnrollment, Long> {

	@Query("SELECT m FROM MarketEnrollment m JOIN FETCH m.member WHERE m.id = :enrollmentId")
	Optional<MarketEnrollment> findByIdFetchWithMember(@Param("enrollmentId") Long enrollmentId);
}
