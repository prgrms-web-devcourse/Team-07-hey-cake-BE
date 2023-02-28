package com.programmers.heycake.domain.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;

public interface OrderCustomRepository {
	List<Order> findAllByMemberIdOrderByVisitDateAsc(
			Long memberId,
			String orderStatus,
			LocalDateTime cursorTime,
			int pageSize
	);

	List<Order> findAllByRegionAndCategoryOrderByCreatedAtAsc(
			Long cursorId,
			int pageSize,
			CakeCategory CakeCategory,
			String region
	);
}
