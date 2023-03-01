package com.programmers.heycake.common.mapper;

import java.time.LocalDateTime;
import java.util.List;

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponse;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetSimpleResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetSimpleServiceResponse;
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

	public static OrderGetResponse toOrderGetResponse(Order order) {
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

	public static OrderGetSimpleServiceResponse toOrderSimpleGetServiceResponse(Order order) {
		return OrderGetSimpleServiceResponse.builder()
				.orderId(order.getId())
				.title(order.getTitle())
				.cakeInfo(order.getCakeInfo())
				.orderStatus(order.getOrderStatus())
				.hopePrice(order.getHopePrice())
				.region(order.getRegion())
				.createdAt(order.getCreatedAt())
				.build();
	}

	public static OrderGetSimpleResponse toOrderSimpleGetResponse(
			OrderGetSimpleServiceResponse orderSimpleGetServiceResponse,
			ImageResponse imageResponse
	) {
		return OrderGetSimpleResponse.builder()
				.orderId(orderSimpleGetServiceResponse.orderId())
				.title(orderSimpleGetServiceResponse.title())
				.cakeInfo(orderSimpleGetServiceResponse.cakeInfo())
				.orderStatus(orderSimpleGetServiceResponse.orderStatus())
				.hopePrice(orderSimpleGetServiceResponse.hopePrice())
				.images(imageResponse.imageUrls())
				.region(orderSimpleGetServiceResponse.region())
				.createdAt(orderSimpleGetServiceResponse.createdAt())
				.build();
	}
}
