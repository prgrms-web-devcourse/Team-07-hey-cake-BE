package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketEnrollmentFacade {

	private static final String ENROLLMENT_IMAGE_PATH = "image/marketEnrollment";

	private final MarketEnrollmentService marketEnrollmentService;
	private final ImageIntegrationService imageIntegrationService;

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
}