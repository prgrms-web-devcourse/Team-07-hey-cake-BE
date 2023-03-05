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

import com.programmers.heycake.domain.market.facade.EnrollmentFacade;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentCreateRequest;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentGetListRequest;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentUpdateStatusRequest;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentDetailWithImageResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentGetListResponse;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

	private final EnrollmentFacade enrollmentFacade;

	@PostMapping
	public ResponseEntity<Void> enrollMarket(@Valid @ModelAttribute EnrollmentCreateRequest request) {
		Long enrollmentId = enrollmentFacade.enrollMarket(request);
		URI location = URI.create("/api/v1/enrollments/" + enrollmentId);
		return ResponseEntity.created(location).build();
	}

	@GetMapping("/{enrollmentId}")
	public ResponseEntity<EnrollmentDetailWithImageResponse> getMarketEnrollment(@PathVariable Long enrollmentId) {
		EnrollmentDetailWithImageResponse enrollment = enrollmentFacade.getMarketEnrollment(enrollmentId);
		return ResponseEntity.ok(enrollment);
	}

	@PatchMapping("/{enrollmentId}")
	public ResponseEntity<Void> changeEnrollmentStatus(
			@PathVariable Long enrollmentId,
			@Valid @RequestBody EnrollmentUpdateStatusRequest request
	) {
		enrollmentFacade.changeEnrollmentStatus(enrollmentId, request.status());
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<EnrollmentGetListResponse> getMarketEnrollments(
			@RequestParam(required = false) Long cursorId,
			@RequestParam Integer pageSize,
			@RequestParam(required = false) EnrollmentStatus status
	) {
		EnrollmentGetListRequest request = new EnrollmentGetListRequest(
				cursorId,
				pageSize,
				status
		);
		EnrollmentGetListResponse marketEnrollments = enrollmentFacade.getMarketEnrollments(request);
		return ResponseEntity.ok(marketEnrollments);
	}
}
