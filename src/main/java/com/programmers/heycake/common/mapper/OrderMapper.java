package com.programmers.heycake.common.mapper;

import java.time.LocalDateTime;
import java.util.List;

import com.programmers.heycake.domain.order.model.dto.OrderDto;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponse;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetResponse;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderMapper {

	public static Order toEntity(OrderDto orderDto) {
		return Order.builder()
				.memberId(orderDto.memberId())
				.title(orderDto.title())
				.orderStatus(orderDto.orderStatus())
				.hopePrice(orderDto.hopePrice())
				.region(orderDto.region())
				.visitDate(orderDto.visitDate())
				.cakeInfo(orderDto.cakeInfo())
				.build();
	}

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

	public static OrderDto toOrderDto(Order order) {
		return OrderDto.builder()
				.id(order.getId())
				.memberId(order.getMemberId())
				.title(order.getTitle())
				.orderStatus(order.getOrderStatus())
				.hopePrice(order.getHopePrice())
				.region(order.getRegion())
				.visitDate(order.getVisitDate())
				.cakeInfo(order.getCakeInfo())
				.build();
	}
}
