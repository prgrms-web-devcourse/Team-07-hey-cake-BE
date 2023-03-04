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

	@Transactional(readOnly = true)
	public OrderGetSimpleResponses getOrders(
			Long cursorId, int pageSize, CakeCategory cakeCategory, String region
	) {
		List<OrderGetServiceSimpleResponse> orderGetSimpleServiceResponses =
				orderService.getOrders(cursorId, pageSize, cakeCategory, region);

		List<OrderGetSimpleResponse> orderGetSimpleResponseList =
				orderGetSimpleServiceResponses
						.stream()
						.map(orderSimpleGetServiceResponse ->
								toOrderGetSimpleResponse(
										orderSimpleGetServiceResponse,
										imageService.getImages(orderSimpleGetServiceResponse.orderId(), ORDER))
						)
						.toList();

		int size = orderGetSimpleResponseList.size();
		long lastCursor = size <= 0 ? 0 : orderGetSimpleResponseList.get(size - 1).orderId();
		boolean isLast = size < pageSize;

		return new OrderGetSimpleResponses(orderGetSimpleResponseList, lastCursor, isLast);
	}
}
