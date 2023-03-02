package com.programmers.heycake.domain.market.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.market.model.dto.EnrollmentControllerResponse;
import com.programmers.heycake.domain.market.model.dto.EnrollmentRequest;
import com.programmers.heycake.domain.market.service.EnrollmentFacade;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class MarketEnrollmentController {

	private final EnrollmentFacade enrollmentFacade;

	// todo 인증 로직 완료 시 입력 인자 수정 필요
	@PostMapping
	public ResponseEntity<Void> enrollMarket(@Valid @ModelAttribute EnrollmentRequest request) {
		Long enrollmentId = enrollmentFacade.enrollMarket(request);
		URI location = URI.create("/api/v1/enrollments/" + enrollmentId);
		return ResponseEntity.created(location).build();
	}

	@GetMapping("/{enrollmentId}")
	public ResponseEntity<EnrollmentControllerResponse> getMarketEnrollment(@PathVariable Long enrollmentId) {
		EnrollmentControllerResponse enrollment = enrollmentFacade.getMarketEnrollment(enrollmentId);
		return ResponseEntity.ok(enrollment);
	}
}
