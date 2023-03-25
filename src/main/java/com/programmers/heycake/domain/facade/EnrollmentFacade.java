package com.programmers.heycake.domain.facade;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.market.mapper.EnrollmentMapper;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentCreateRequest;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentsRequest;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentDetailResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentsElementResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentsResponse;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;
import com.programmers.heycake.domain.market.service.EnrollmentService;
import com.programmers.heycake.domain.market.service.MarketService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentFacade {

	private static final String ENROLLMENT_IMAGE_PATH = "images/marketEnrollments";

	private final EnrollmentService enrollmentService;
	private final ImageIntegrationService imageIntegrationService;
	private final MarketService marketService;

	@Transactional
	public Long createEnrollment(EnrollmentCreateRequest request) {

		Long enrollmentId = enrollmentService.createEnrollment(request);

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
	public EnrollmentDetailResponse getMarketEnrollment(Long enrollmentId) {
		MarketEnrollment enrollment = enrollmentService.getMarketEnrollment(enrollmentId);
		ImageResponses images = imageIntegrationService.getImages(enrollmentId, ENROLLMENT_MARKET);
		return EnrollmentMapper.toEnrollmentDetailResponse(enrollment, images);
	}

	@Transactional
	public void updateEnrollmentStatus(Long enrollmentId, EnrollmentStatus status) {
		enrollmentService.updateEnrollmentStatus(enrollmentId, status);

		if (status == EnrollmentStatus.APPROVED) {
			marketService.createMarket(enrollmentId);
		}
	}

	@Transactional(readOnly = true)
	public EnrollmentsResponse getMarketEnrollments(EnrollmentsRequest request) {
		List<MarketEnrollment> enrollments = enrollmentService.getMarketEnrollments(request);
		List<EnrollmentsElementResponse> withImageResponses = enrollments.stream()
				.map(enrollment -> {
					ImageResponses images = imageIntegrationService.getImages(enrollment.getId(), ENROLLMENT_MARKET);
					return EnrollmentMapper.toEnrollmentsElementResponse(enrollment, images);
				})
				.toList();

		Long nextCursor =
				withImageResponses.size() < request.pageSize() ?
						0 : withImageResponses.get(withImageResponses.size() - 1).enrollmentId();

		return EnrollmentMapper.toEnrollmentsResponse(withImageResponses, nextCursor);
	}
}