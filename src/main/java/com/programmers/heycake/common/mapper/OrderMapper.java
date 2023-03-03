package com.programmers.heycake.common.mapper;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;
import static com.programmers.heycake.domain.order.model.vo.OrderStatus.*;
import static lombok.AccessLevel.*;

import com.programmers.heycake.domain.order.model.dto.request.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.entity.CakeInfo;
import com.programmers.heycake.domain.order.model.entity.Order;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class OrderMapper {
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
}
