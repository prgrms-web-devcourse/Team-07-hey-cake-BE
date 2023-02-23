package com.programmers.heycake.domain.market.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentRequest;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentResponse;
import com.programmers.heycake.domain.market.service.MarketEnrollmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class MarketEnrollmentController {

	private final MarketEnrollmentService marketEnrollmentService;

	@PostMapping
	public ResponseEntity<Void> enrollMarket(@Valid @ModelAttribute MarketEnrollmentRequest marketEnrollmentRequest) {
		MarketEnrollmentResponse marketEnrollmentResponse = marketEnrollmentService.enrollMarket(marketEnrollmentRequest);
		URI location = URI.create("/api/v1/enrollments/" + marketEnrollmentResponse.id());
		return ResponseEntity.created(location).build();
	}
}
