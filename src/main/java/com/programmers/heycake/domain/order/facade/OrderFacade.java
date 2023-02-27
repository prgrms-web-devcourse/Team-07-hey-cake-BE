package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.order.model.dto.OrderCreateRequest;
import com.programmers.heycake.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderFacade {
	private final OrderService orderService;

	private static final String SUB_PATH = "image";

	private final ImageIntegrationService imageIntegrationService;

	@Transactional
	public void createOrder(OrderCreateRequest orderCreateRequest) {
		Long orderId = orderService.create(orderCreateRequest);
		if (orderCreateRequest.cakeCategory() != null) {
			for (int i = 0; i < orderCreateRequest.cakeImages().size(); i++) {
				imageIntegrationService.createAndUploadImage(
						orderCreateRequest.cakeImages().get(i),
						SUB_PATH,
						orderId,
						ORDER
				);
			}
		}
	}
}
