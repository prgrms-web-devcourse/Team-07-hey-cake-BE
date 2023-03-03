package com.programmers.heycake.domain.order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.programmers.heycake.common.mapper.OrderMapper;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetServiceSimpleResponse;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
import com.programmers.heycake.domain.order.repository.OrderQueryDslRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderQueryDslRepository orderQueryDslRepository;

	public List<OrderGetServiceSimpleResponse> getOrders(
			Long cursorId, int pageSize, CakeCategory cakeCategory, String region
	) {
		return orderQueryDslRepository
				.findAllByRegionAndCategoryOrderByCreatedAtAsc(cursorId, pageSize, cakeCategory, region)
				.stream()
				.map(OrderMapper::toOrderGetServiceSimpleResponse)
				.collect(Collectors.toList());
	}
}
