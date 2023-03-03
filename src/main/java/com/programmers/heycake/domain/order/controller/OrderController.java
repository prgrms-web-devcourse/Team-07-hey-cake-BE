package com.programmers.heycake.domain.order.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.member.model.dto.response.OrderGetDetailResponse;
import com.programmers.heycake.domain.order.facade.OrderFacade;
import com.programmers.heycake.domain.order.model.dto.request.MyOrderRequest;
import com.programmers.heycake.domain.order.model.dto.request.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrderResponseList;
import com.programmers.heycake.domain.order.model.dto.response.OrderGetSimpleResponses;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderFacade orderFacade;

	@PostMapping
	public ResponseEntity<String> createOrder(
			@Valid @ModelAttribute OrderCreateRequest orderCreateRequest,
			HttpServletRequest httpServletRequest
	) {
		Long orderId = orderFacade.createOrder(orderCreateRequest);
		return ResponseEntity.created(
				URI.create(httpServletRequest.getRequestURI() + "/" + orderId)
		).build();
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<OrderGetDetailResponse> getOrder(@PathVariable Long orderId) {
		return ResponseEntity.ok(orderFacade.getOrder(orderId));
	}

	@GetMapping("/my")
	public ResponseEntity<MyOrderResponseList> getOrderList(@RequestBody @Valid MyOrderRequest getOrderRequest) {
		MyOrderResponseList myOrderList = orderFacade.getMyOrderList(getOrderRequest);

		return ResponseEntity.ok(myOrderList);
	}

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
