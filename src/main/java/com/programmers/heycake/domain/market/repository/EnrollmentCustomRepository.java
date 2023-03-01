package com.programmers.heycake.domain.market.repository;

import java.util.List;

import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;

public interface EnrollmentCustomRepository {

	List<MarketEnrollment> findAllOrderByCreatedAtDesc(
			Long cursorEnrollmentId,
			Integer pageSize,
			EnrollmentStatus status
	);
}
