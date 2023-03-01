package com.programmers.heycake.common.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.order.model.dto.OrderDto;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponse;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetDetailResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetDetailServiceResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetServiceSimpleResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetSimpleResponse;
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

	public static OrderGetDetailServiceResponse toOrderGetServiceDetailResponse(Order order) {
		return OrderGetDetailServiceResponse.builder()
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
						.map(ImageResponse::imageUrls)
						.collect(Collectors.toList())
				)
				.region(orderSimpleGetServiceResponse.region())
				.visitTime(orderSimpleGetServiceResponse.visitDate())
				.createdAt(orderSimpleGetServiceResponse.createdAt())
				.build()
				;
	}

	public static OrderGetDetailResponse toOrderGetDetailResponse(
			OrderGetDetailServiceResponse orderGetDetailServiceResponse,
			ImageResponses imageResponses
	) {
		return OrderGetDetailResponse.builder()
				.orderId(orderGetDetailServiceResponse.orderId())
				.memberId(orderGetDetailServiceResponse.memberId())
				.title(orderGetDetailServiceResponse.title())
				.region(orderGetDetailServiceResponse.region())
				.orderStatus(orderGetDetailServiceResponse.orderStatus())
				.hopePrice(orderGetDetailServiceResponse.hopePrice())
				.visitDate(orderGetDetailServiceResponse.visitDate())
				.cakeInfo(orderGetDetailServiceResponse.cakeInfo())
				.offerCount(orderGetDetailServiceResponse.offerCount())
				.images(imageResponses.images()
						.stream()
						.map(ImageResponse::imageUrls)
						.collect(Collectors.toList())
				)
				.offerCount(orderGetDetailServiceResponse.offerCount())
				.createdAt(orderGetDetailServiceResponse.createdAt())
				.updatedAt(orderGetDetailServiceResponse.updatedAt())
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
