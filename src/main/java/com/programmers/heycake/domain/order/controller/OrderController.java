package com.programmers.heycake.domain.order.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.facade.OrderFacade;
import com.programmers.heycake.domain.member.model.dto.response.OrderDetailResponse;
import com.programmers.heycake.domain.order.model.dto.request.MyOrdersRequest;
import com.programmers.heycake.domain.order.model.dto.request.OrderCreateRequest;
import com.programmers.heycake.domain.order.model.dto.response.MyOrdersResponse;
import com.programmers.heycake.domain.order.model.dto.response.OrdersResponse;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;

import lombok.RequiredArgsConstructor;

@Validated
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

	@GetMapping
	public ResponseEntity<OrdersResponse> getOrders(
			@RequestParam(required = false) Long cursorId,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(required = false) CakeCategory cakeCategory,
			@RequestParam(required = false) OrderStatus orderStatus,
			@RequestParam(required = false) String region
	) {
		return ResponseEntity.ok(orderFacade.getOrders(cursorId, pageSize, cakeCategory, orderStatus, region));
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<OrderDetailResponse> getOrder(@PathVariable Long orderId) {
		return ResponseEntity.ok(orderFacade.getOrder(orderId));
	}

	@GetMapping("/my")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MARKET')")
	public ResponseEntity<MyOrdersResponse> getMyOrders(
			@RequestParam(required = false) @Positive Long cursorId,
			@RequestParam(defaultValue = "10") @Positive Integer pageSize,
			@RequestParam(required = false) OrderStatus orderStatus
	) {
		MyOrdersRequest myOrderRequest = new MyOrdersRequest(cursorId, pageSize, orderStatus);
		MyOrdersResponse myOrders = orderFacade.getMyOrders(myOrderRequest);

		return ResponseEntity.ok(myOrders);
	}

	@DeleteMapping("/{orderId}")
	public ResponseEntity<Void> deleteOrder(@PathVariable @Positive Long orderId) {
		orderFacade.deleteOrder(orderId);
		return ResponseEntity.noContent().build();
	}
}
