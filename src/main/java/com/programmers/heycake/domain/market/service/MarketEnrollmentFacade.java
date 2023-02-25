package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.service.ImageS3Service;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketEnrollmentFacade {

	private static final String ENROLLMENT_IMAGE_PATH = "image/marketEnrollment";

	private final MarketEnrollmentService marketEnrollmentService;
	private final ImageS3Service imageS3Service;
	private final ImageService imageService;

	@Transactional
	public Long enrollMarket(MarketEnrollmentRequest request) {

		Long enrollmentId = marketEnrollmentService.enrollMarket(request);

		String licenseImgUrl = imageS3Service.upload(request.businessLicenseImage(), ENROLLMENT_IMAGE_PATH);
		String marketImgUrl = imageS3Service.upload(request.marketImage(), ENROLLMENT_IMAGE_PATH);
		imageService.createImage(enrollmentId, ENROLLMENT_LICENSE, licenseImgUrl);
		imageService.createImage(enrollmentId, ENROLLMENT_MARKET, marketImgUrl);

		return enrollmentId;
	}
}
