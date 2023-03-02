package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.market.mapper.EnrollmentMapper;
import com.programmers.heycake.domain.market.model.dto.EnrollmentControllerResponse;
import com.programmers.heycake.domain.market.model.dto.EnrollmentRequest;
import com.programmers.heycake.domain.market.model.dto.EnrollmentResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentFacade {

	private static final String ENROLLMENT_IMAGE_PATH = "images/marketEnrollments";

	private final EnrollmentService enrollmentService;
	private final ImageIntegrationService imageIntegrationService;

	@Transactional
	public Long enrollMarket(EnrollmentRequest request) {

		Long enrollmentId = enrollmentService.enrollMarket(request);

		imageIntegrationService.createAndUploadImage(
				request.businessLicenseImage(),
				ENROLLMENT_IMAGE_PATH,
				enrollmentId,
				ENROLLMENT_LICENSE
		);
		imageIntegrationService.createAndUploadImage(
				request.marketImage(),
				ENROLLMENT_IMAGE_PATH,
				enrollmentId,
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
}