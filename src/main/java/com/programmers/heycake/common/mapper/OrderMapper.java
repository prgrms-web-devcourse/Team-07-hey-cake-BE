package com.programmers.heycake.common.mapper;

import java.time.LocalDateTime;
import java.util.List;

import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponse;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetResponse;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderMapper {
	public static MyOrderResponseList toGetOrderResponseListForMember(List<Order> orderList, LocalDateTime lastTime) {
		List<MyOrderResponse> getOrderResponseList =
				orderList
						.stream()
						.map(order -> new MyOrderResponse(
								order.getId(),
								order.getTitle(),
								order.getOrderStatus(),
								order.getRegion(),
								order.getVisitDate(),
								order.getCreatedAt()
						)).toList();
		return new MyOrderResponseList(getOrderResponseList, lastTime);
	}

	public static MyOrderResponseList toGetOrderResponseListForMarket(
			List<OrderHistory> orderHistoryList,
			LocalDateTime lastTime
	) {
		List<Order> orderList =
				orderHistoryList
						.stream()
						.map(OrderHistory::getOrder)
						.toList();

		return toGetOrderResponseListForMember(orderList, lastTime);
	}

	public static OrderGetResponse toGetOrderResponse(Order order) {
		return OrderGetResponse.builder()
				.orderId(order.getId())
				.memberId(order.getMemberId())
				.title(order.getTitle())
				.cakeInfo(order.getCakeInfo())
				.orderStatus(order.getOrderStatus())
				.visitDate(order.getVisitDate())
				.hopePrice(order.getHopePrice())
				.region(order.getRegion())
				.offerCount(order.getOffers().size())
				.createdAt(order.getCreatedAt())
				.updatedAt(order.getUpdatedAt())
				.build();
	}
}
