package com.programmers.heycake.domain.order.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.order.facade.OrderFacade;
import com.programmers.heycake.domain.order.model.dto.request.GetOrderRequest;
import com.programmers.heycake.domain.order.model.dto.response.GetOrderResponseList;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderFacade orderFacade;

	@GetMapping("/my")
	public ResponseEntity<GetOrderResponseList> getOrderList(@RequestBody @Valid GetOrderRequest getOrderRequest) {
		GetOrderResponseList orderList = orderFacade.getOrderList(getOrderRequest);

		return ResponseEntity.ok(orderList);
	}
}
