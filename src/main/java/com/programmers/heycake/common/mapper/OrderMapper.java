package com.programmers.heycake.common.mapper;

import static lombok.AccessLevel.*;

import java.util.stream.Collectors;

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetServiceSimpleResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetSimpleResponse;
import com.programmers.heycake.domain.order.model.entity.Order;

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
						.toList()
				)
				.region(orderSimpleGetServiceResponse.region())
				.visitTime(orderSimpleGetServiceResponse.visitDate())
				.createdAt(orderSimpleGetServiceResponse.createdAt())
				.build()
				;
	}
}
