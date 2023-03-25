package com.programmers.heycake.domain.market.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
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

import com.programmers.heycake.domain.facade.EnrollmentFacade;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentCreateRequest;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentUpdateStatusRequest;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentsRequest;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentDetailResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentsResponse;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

	private final EnrollmentFacade enrollmentFacade;

	@PostMapping
	public ResponseEntity<Void> createEnrollment(
			@Valid @ModelAttribute EnrollmentCreateRequest createRequest,
			HttpServletRequest request
	) {
		Long enrollmentId = enrollmentFacade.createEnrollment(createRequest);
		URI location = URI.create(request.getRequestURI() + "/" + enrollmentId);
		return ResponseEntity.created(location).build();
	}

	@GetMapping("/{enrollmentId}")
	public ResponseEntity<EnrollmentDetailResponse> getMarketEnrollment(@PathVariable Long enrollmentId) {
		EnrollmentDetailResponse enrollment = enrollmentFacade.getMarketEnrollment(enrollmentId);
		return ResponseEntity.ok(enrollment);
	}

	@PatchMapping("/{enrollmentId}")
	public ResponseEntity<Void> updateEnrollmentStatus(
			@PathVariable Long enrollmentId,
			@Valid @RequestBody EnrollmentUpdateStatusRequest request
	) {
		enrollmentFacade.updateEnrollmentStatus(enrollmentId, request.status());
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<EnrollmentsResponse> getMarketEnrollments(
			@RequestParam(required = false) Long cursorId,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(required = false) EnrollmentStatus status
	) {
		EnrollmentsRequest request = new EnrollmentsRequest(
				cursorId,
				pageSize,
				status
		);
		EnrollmentsResponse marketEnrollments = enrollmentFacade.getMarketEnrollments(request);
		return ResponseEntity.ok(marketEnrollments);
	}
}
