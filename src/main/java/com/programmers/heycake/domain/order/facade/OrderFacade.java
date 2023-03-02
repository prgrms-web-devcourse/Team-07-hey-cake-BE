package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.order.model.dto.request.OrderCreateRequest;
import com.programmers.heycake.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderFacade {
	private final OrderService orderService;

	private static final String ORDER_IMAGE_SUB_PATH = "images/orders";

	private final ImageIntegrationService imageIntegrationService;

	@Transactional
	public void createOrder(OrderCreateRequest orderCreateRequest) {
		Long orderId = orderService.create(orderCreateRequest);

		orderCreateRequest.cakeImages()
				.forEach(
						cakeImage ->
								imageIntegrationService.createAndUploadImage(
										cakeImage,
										ORDER_IMAGE_SUB_PATH,
										orderId,
										ORDER
								))
		;
	}
}