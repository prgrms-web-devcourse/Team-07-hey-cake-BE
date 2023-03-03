package com.programmers.heycake.common.mapper;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetServiceSimpleResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetSimpleResponse;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class OrderMapper {

	public static OrderGetServiceSimpleResponse toOrderGetServiceSimpleResponse(Order order) {
		return OrderGetServiceSimpleResponse.builder()
				.orderId(order.getId())
				.title(order.getTitle())
				.cakeInfo(order.getCakeInfo())
				.orderStatus(order.getOrderStatus())
				.hopePrice(order.getHopePrice())
				.region(order.getRegion())
				.visitDate(order.getVisitDate())
				.createdAt(order.getCreatedAt())
				.build();
	}

	public static OrderGetSimpleResponse toOrderGetSimpleResponse(
			OrderGetServiceSimpleResponse orderSimpleGetServiceResponse,
			ImageResponses imageResponses
	) {
		return OrderGetSimpleResponse.builder()
				.orderId(orderSimpleGetServiceResponse.orderId())
				.title(orderSimpleGetServiceResponse.title())
				.cakeInfo(orderSimpleGetServiceResponse.cakeInfo())
				.orderStatus(orderSimpleGetServiceResponse.orderStatus())
				.hopePrice(orderSimpleGetServiceResponse.hopePrice())
				.images(imageResponses.images()
						.stream()
						.map(ImageResponse::imageUrl)
						.collect(Collectors.toList())
				)
				.region(orderSimpleGetServiceResponse.region())
				.visitTime(orderSimpleGetServiceResponse.visitDate())
				.createdAt(orderSimpleGetServiceResponse.createdAt())
				.build()
				;
	}

	//TODO 추후에 삭제
	public static MyOrderResponseList toMyOrderResponseListForMember(
			LocalDateTime lastTime,
			List<Order> orderList) {
		List<MyOrderResponse> getOrderResponseList =
				orderList
						.stream()
						.map(order -> MyOrderResponse.builder()
								.id(order.getId())
								.title(order.getTitle())
								.orderStatus(order.getOrderStatus())
								.region(order.getRegion())
								.visitTime(order.getVisitDate())
								.createdAt(order.getCreatedAt())
								.imageUrl(null)
								.build()
						).toList();
		return new MyOrderResponseList(getOrderResponseList, lastTime);
	}

	public static MyOrderResponseList toGetOrderResponseListForMarket(
			//TOO 나중에 수정
			List<OrderHistory> orderHistoryList,
			LocalDateTime lastTime
	) {
		List<Order> orderList =
				orderHistoryList
						.stream()
						.map(OrderHistory::getOrder)
						.toList();

		//TODO 추후에 변경
		return toMyOrderResponseListForMember(lastTime, orderList);
	}

}
