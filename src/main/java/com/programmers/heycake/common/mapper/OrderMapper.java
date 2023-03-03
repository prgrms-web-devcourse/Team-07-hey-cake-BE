package com.programmers.heycake.common.mapper;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;
import static com.programmers.heycake.domain.order.model.vo.OrderStatus.*;
import static lombok.AccessLevel.*;

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.member.model.dto.response.OrderGetDetailResponse;
import com.programmers.heycake.domain.order.model.dto.OrderDto;
import com.programmers.heycake.domain.order.model.dto.request.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetDetailServiceResponse;
import com.programmers.heycake.domain.order.model.entity.CakeInfo;
import com.programmers.heycake.domain.order.model.entity.Order;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
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

	public static Order toEntity(OrderCreateRequest orderCreateRequest, CakeInfo cakeInfo) {
		return Order.builder()
				.cakeInfo(cakeInfo)
				.hopePrice(orderCreateRequest.hopePrice())
				.memberId(getMemberId())
				.orderStatus(NEW)
				.visitDate(orderCreateRequest.visitTime())
				.title(orderCreateRequest.title())
				.region(orderCreateRequest.region())
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

	public static OrderGetDetailServiceResponse toOrderGetDetailServiceResponse(Order order) {
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
						.map(ImageResponse::imageUrl)
						.toList()
				)
				.offerCount(orderGetDetailServiceResponse.offerCount())
				.createdAt(orderGetDetailServiceResponse.createdAt())
				.updatedAt(orderGetDetailServiceResponse.updatedAt())
				.build();
	}
}
