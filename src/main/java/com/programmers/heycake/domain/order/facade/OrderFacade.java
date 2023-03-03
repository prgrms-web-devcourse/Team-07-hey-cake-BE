package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.common.mapper.OrderMapper.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.member.model.dto.response.OrderGetDetailResponse;
import com.programmers.heycake.domain.order.dto.response.OrderGetDetailServiceResponse;
import com.programmers.heycake.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderFacade {
	private final OrderService orderService;
	private final ImageService imageService;

	@Transactional
	public OrderGetDetailResponse getOrder(Long orderId) {
		OrderGetDetailServiceResponse orderGetDetailServiceResponse = orderService.getOrder(orderId);
		ImageResponses imageResponses = imageService.getImages(orderGetDetailServiceResponse.orderId(), ORDER);
		return toOrderGetDetailResponse(orderGetDetailServiceResponse, imageResponses);
	}
}