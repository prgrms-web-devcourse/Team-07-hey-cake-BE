package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.common.mapper.OrderMapper.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.member.model.dto.response.OrderGetDetailResponse;
import com.programmers.heycake.domain.order.dto.response.OrderGetDetailServiceResponse;
import com.programmers.heycake.domain.order.model.dto.request.OrderCreateRequest;
import com.programmers.heycake.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderFacade {
	private static final String ORDER_IMAGE_SUB_PATH = "images/orders";
	private final OrderService orderService;
	private final ImageIntegrationService imageIntegrationService;
	private final ImageService imageService;

	@Transactional
	public Long createOrder(OrderCreateRequest orderCreateRequest) {
		Long orderId = orderService.create(orderCreateRequest);

		orderCreateRequest.cakeImages()
				.forEach(
						cakeImage ->
								imageIntegrationService.createAndUploadImage(
										cakeImage,
										ORDER_IMAGE_SUB_PATH,
										orderId,
										ORDER
								));
		return orderId;
	}

	@Transactional
	public OrderGetDetailResponse getOrder(Long orderId) {
		OrderGetDetailServiceResponse orderGetDetailServiceResponse = orderService.getOrder(orderId);
		ImageResponses imageResponses = imageService.getImages(orderGetDetailServiceResponse.orderId(), ORDER);
		return toOrderGetDetailResponse(orderGetDetailServiceResponse, imageResponses);
	}
}