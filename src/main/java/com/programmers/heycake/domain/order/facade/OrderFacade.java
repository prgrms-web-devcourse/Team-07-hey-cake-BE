package com.programmers.heycake.domain.order.facade;

import static com.programmers.heycake.common.mapper.OrderMapper.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetServiceSimpleResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetSimpleResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetSimpleResponses;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
import com.programmers.heycake.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderFacade {

	private final OrderService orderService;
	private final ImageService imageService;

	@Transactional
	public OrderGetSimpleResponses getOrders(
			Long cursorId, int pageSize, CakeCategory cakeCategory, String region
	) {
		List<OrderGetServiceSimpleResponse> orderGetSimpleServiceResponses =
				orderService.getOrders(cursorId, pageSize, cakeCategory, region);

		List<OrderGetSimpleResponse> orderGetSimpleResponses =
				orderGetSimpleServiceResponses
						.stream()
						.map(orderSimpleGetServiceResponse ->
								toOrderGetSimpleResponse(
										orderSimpleGetServiceResponse,
										imageService.getImages(orderSimpleGetServiceResponse.orderId(), ORDER))
						)
						.toList();

		int size = orderGetSimpleResponses.size();
		long lastCursor = 0;

		for (OrderGetSimpleResponse orderGetSimpleResponse : orderGetSimpleResponses) {
			lastCursor = orderGetSimpleResponse.orderId();
		}

		if (size < pageSize) {
			return new OrderGetSimpleResponses(orderGetSimpleResponses, lastCursor, true);
		}
		return new OrderGetSimpleResponses(orderGetSimpleResponses, lastCursor, false);
	}
}
