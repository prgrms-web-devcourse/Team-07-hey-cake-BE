package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.market.event.EnrollmentStatusEvent;
import com.programmers.heycake.domain.market.mapper.MarketEnrollmentMapper;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentControllerResponse;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentRequest;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentResponse;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketEnrollmentFacade {

	private static final String ENROLLMENT_IMAGE_PATH = "image/marketEnrollment";

	private final MarketEnrollmentService marketEnrollmentService;
	private final ImageIntegrationService imageIntegrationService;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public Long enrollMarket(MarketEnrollmentRequest request) {

		Long enrollmentId = marketEnrollmentService.enrollMarket(request);

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
	public MarketEnrollmentControllerResponse getMarketEnrollment(Long enrollmentId) {
		MarketEnrollmentResponse enrollment = marketEnrollmentService.getMarketEnrollment(enrollmentId);
		ImageResponses images = imageIntegrationService.getImages(enrollmentId, ENROLLMENT_MARKET);
		return MarketEnrollmentMapper.toControllerResponse(enrollment, images);
	}

	@Transactional
	public void changeEnrollmentStatus(Long enrollmentId, EnrollmentStatus status) {
		marketEnrollmentService.changeEnrollmentStatus(enrollmentId, status);
		applicationEventPublisher.publishEvent(new EnrollmentStatusEvent(enrollmentId, status));
	}
}