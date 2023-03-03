package com.programmers.heycake.domain.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.order.facade.OrderFacade;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetSimpleResponses;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderFacade orderFacade;

	@GetMapping
	public ResponseEntity<OrderGetSimpleResponses> getOrders(
			@RequestParam(required = false) Long cursorId,
			@RequestParam int pageSize,
			@RequestParam(required = false) CakeCategory cakeCategory,
			@RequestParam(required = false) String region
	) {
		return ResponseEntity.ok(orderFacade.getOrders(cursorId, pageSize, cakeCategory, region));
	}
}

