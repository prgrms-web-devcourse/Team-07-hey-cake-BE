package com.programmers.heycake.domain.market.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentControllerResponse;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentListRequest;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentRequest;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentResponses;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentStatusRequest;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;
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

	@GetMapping("/{enrollmentId}")
	public ResponseEntity<MarketEnrollmentControllerResponse> getMarketEnrollment(@PathVariable Long enrollmentId) {
		MarketEnrollmentControllerResponse enrollment = marketEnrollmentFacade.getMarketEnrollment(enrollmentId);
		return ResponseEntity.ok(enrollment);
	}

	@PatchMapping("/{enrollmentId}")
	public ResponseEntity<Void> changeEnrollmentStatus(
			@PathVariable Long enrollmentId,
			@Valid @RequestBody MarketEnrollmentStatusRequest request
	) {
		marketEnrollmentFacade.changeEnrollmentStatus(enrollmentId, EnrollmentStatus.valueOf(request.status()));
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<MarketEnrollmentResponses> getMarketEnrollments(
			@RequestParam(required = false) Long cursor,
			@RequestParam(required = false) Integer pageSize,
			@RequestParam(required = false) EnrollmentStatus enrollmentStatus
	) {
		MarketEnrollmentListRequest request = new MarketEnrollmentListRequest(
				cursor,
				pageSize,
				enrollmentStatus
		);
		MarketEnrollmentResponses marketEnrollments = marketEnrollmentFacade.getMarketEnrollments(request);
		return ResponseEntity.ok(marketEnrollments);
	}
}
