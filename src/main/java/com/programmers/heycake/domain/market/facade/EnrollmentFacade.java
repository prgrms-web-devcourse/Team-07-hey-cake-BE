package com.programmers.heycake.domain.market.facade;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.mapper.EnrollmentMapper;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentCreateRequest;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentGetListRequest;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentDetailNoImageResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentDetailWithImageResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentGetListResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentListSummaryNoImageResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentListSummaryWithImageResponse;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;
import com.programmers.heycake.domain.market.service.EnrollmentService;
import com.programmers.heycake.domain.market.service.MarketService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentFacade {

	private static final String ENROLLMENT_IMAGE_PATH = "images/marketEnrollments";

	private final EnrollmentService enrollmentService;
	private final ImageService imageService;
	private final MarketService marketService;

	@Transactional
	public Long createEnrollment(EnrollmentCreateRequest request) {

		Long enrollmentId = enrollmentService.createEnrollment(request);

		imageService.createAndUploadImage(
				request.businessLicenseImage(),
				ENROLLMENT_IMAGE_PATH,
				enrollmentId,
				ENROLLMENT_LICENSE
		);
		imageService.createAndUploadImage(
				request.marketImage(),
				ENROLLMENT_IMAGE_PATH,
				enrollmentId,
				ENROLLMENT_MARKET
		);

		return enrollmentId;
	}

	@Transactional(readOnly = true)
	public EnrollmentDetailWithImageResponse getMarketEnrollment(Long enrollmentId) {
		EnrollmentDetailNoImageResponse enrollment = enrollmentService.getMarketEnrollment(enrollmentId);
		ImageResponses images = imageService.getImages(enrollmentId, ENROLLMENT_MARKET);
		return EnrollmentMapper.toEnrollmentDetailWithImageResponse(enrollment, images);
	}

	@Transactional
	public void changeEnrollmentStatus(Long enrollmentId, EnrollmentStatus status) {
		enrollmentService.changeEnrollmentStatus(enrollmentId, status);

		if (status == EnrollmentStatus.APPROVED) {
			marketService.enrollMarket(enrollmentId);
		}
	}

	@Transactional(readOnly = true)
	public EnrollmentGetListResponse getMarketEnrollments(EnrollmentGetListRequest request) {
		List<EnrollmentListSummaryNoImageResponse> noImageResponses = enrollmentService.getMarketEnrollments(request);
		List<EnrollmentListSummaryWithImageResponse> withImageResponses = noImageResponses.stream()
				.map(enrollment -> {
					ImageResponses images = imageService.getImages(enrollment.enrollmentId(), ENROLLMENT_MARKET);
					return EnrollmentMapper.toEnrollmentListSummaryWithImageResponse(enrollment, images);
				})
				.toList();
		Long nextCursor =
				withImageResponses.size() < request.pageSize() ?
						0 : withImageResponses.get(withImageResponses.size() - 1).enrollmentId();

		return EnrollmentMapper.toEnrollmentGetListResponse(withImageResponses, nextCursor);
	}
}