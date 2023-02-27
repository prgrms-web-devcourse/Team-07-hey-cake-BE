package com.programmers.heycake.domain.market.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentRequest;
import com.programmers.heycake.domain.market.service.MarketEnrollmentFacade;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class MarketEnrollmentController {

	private final MarketEnrollmentFacade marketEnrollmentFacade;

	// todo 인증 로직 완료 시 입력 인자 수정 필요
	@PostMapping
	public ResponseEntity<Void> enrollMarket(@Valid @ModelAttribute MarketEnrollmentRequest request) {
		Long enrollmentId = marketEnrollmentFacade.enrollMarket(request);
		URI location = URI.create("/api/v1/enrollments/" + enrollmentId);
		return ResponseEntity.created(location).build();
	}
}