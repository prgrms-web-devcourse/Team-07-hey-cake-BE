package com.programmers.heycake.common.mapper;

import java.time.LocalDateTime;
import java.util.List;

import com.programmers.heycake.domain.order.model.dto.response.GetOrderResponse;
import com.programmers.heycake.domain.order.model.dto.response.GetOrderResponseList;
import com.programmers.heycake.domain.order.model.entity.Order;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderMapper {
	public static GetOrderResponseList toGetOrderResponseList(List<Order> orderList, LocalDateTime lastTime) {
		List<GetOrderResponse> getOrderResponseList =
				orderList
						.stream()
						.map(order -> new GetOrderResponse(
								order.getId(),
								order.getTitle(),
								order.getOrderStatus(),
								order.getRegion(),
								order.getVisitDate(),
								order.getCreatedAt()
						)).toList();
		return new GetOrderResponseList(getOrderResponseList, lastTime);
	}
}
