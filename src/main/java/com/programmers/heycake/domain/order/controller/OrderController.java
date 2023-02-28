package com.programmers.heycake.domain.order.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.order.facade.OrderFacade;
import com.programmers.heycake.domain.order.model.dto.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.request.MyOrderRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderFacade orderFacade;

	@PostMapping
	public ResponseEntity<Void> createOrder(
			@Valid @ModelAttribute OrderCreateRequest orderCreateRequest
	) {
		orderFacade.createOrder(orderCreateRequest);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/my")
	public ResponseEntity<MyOrderResponseList> getOrderList(@RequestBody @Valid MyOrderRequest getOrderRequest) {
		MyOrderResponseList myOrderList = orderFacade.getMyOrderList(getOrderRequest);

		return ResponseEntity.ok(myOrderList);
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<OrderGetResponse> getOrder(@PathVariable Long orderId) {
		return ResponseEntity.ok(orderFacade.getOrder(orderId));
	}
}