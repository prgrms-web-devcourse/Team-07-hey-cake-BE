package com.programmers.heycake.domain.order.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.order.facade.OrderFacade;
import com.programmers.heycake.domain.order.model.dto.request.OrderCreateRequest;

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
}
