package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.market.event.EnrollmentStatusEvent;
import com.programmers.heycake.domain.market.mapper.EnrollmentMapper;
import com.programmers.heycake.domain.market.model.dto.EnrollmentControllerResponse;
import com.programmers.heycake.domain.market.model.dto.EnrollmentListRequest;
import com.programmers.heycake.domain.market.model.dto.EnrollmentListResponse;
import com.programmers.heycake.domain.market.model.dto.EnrollmentRequest;
import com.programmers.heycake.domain.market.model.dto.EnrollmentResponse;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentFacade {

	private static final String ENROLLMENT_IMAGE_PATH = "images/marketEnrollments";

	private final EnrollmentService enrollmentService;
	private final ImageIntegrationService imageIntegrationService;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public Long enrollMarket(EnrollmentRequest request) {

		Long enrollmentId = enrollmentService.enrollMarket(request);

		imageIntegrationService.createAndUploadImage(
				request.businessLicenseImage(),
				ENROLLMENT_IMAGE_PATH, enrollmentId,
				ENROLLMENT_LICENSE
		);
		imageIntegrationService.createAndUploadImage(
				request.marketImage(),
				ENROLLMENT_IMAGE_PATH, enrollmentId,
				ENROLLMENT_MARKET
		);

		return enrollmentId;
	}

	@Transactional(readOnly = true)
	public EnrollmentControllerResponse getMarketEnrollment(Long enrollmentId) {
		EnrollmentResponse enrollment = enrollmentService.getMarketEnrollment(enrollmentId);
		ImageResponses images = imageIntegrationService.getImages(enrollmentId, ENROLLMENT_MARKET);
		return EnrollmentMapper.toControllerResponse(enrollment, images);
	}

	@Transactional
	public void changeEnrollmentStatus(Long enrollmentId, EnrollmentStatus status) {
		applicationEventPublisher.publishEvent(new EnrollmentStatusEvent(enrollmentId, status));
		enrollmentService.changeEnrollmentStatus(enrollmentId, status);
	}

	@Transactional(readOnly = true)
	public EnrollmentListResponse getMarketEnrollments(EnrollmentListRequest request) {
		return enrollmentService.getMarketEnrollments(request);
	}
}