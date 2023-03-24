package com.programmers.heycake.common.mapper;

import static com.programmers.heycake.common.exception.ErrorCode.*;

import java.util.List;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentCreateRequest;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentDetailResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentsComponentResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentsResponse;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.model.vo.MarketAddress;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnrollmentMapper {

	public static MarketEnrollment toEntity(EnrollmentCreateRequest request) {
		return MarketEnrollment.builder()
				.businessNumber(request.businessNumber())
				.ownerName(request.ownerName())
				.openDate(request.openDate())
				.marketName(request.marketName())
				.phoneNumber(request.phoneNumber())
				.marketAddress(new MarketAddress(
						request.city(),
						request.district(),
						request.detailAddress())
				)
				.openTime(request.openTime())
				.endTime(request.endTime())
				.description(request.description())
				.build();
	}

	public static EnrollmentDetailResponse toEnrollmentDetailResponse(
			MarketEnrollment enrollment,
			ImageResponses images
	) {
		return EnrollmentDetailResponse.builder()
				.phoneNumber(enrollment.getPhoneNumber())
				.marketAddress(enrollment.getMarketAddress())
				.openTime(enrollment.getOpenTime())
				.endTime(enrollment.getEndTime())
				.description(enrollment.getDescription())
				.marketName(enrollment.getMarketName())
				.businessNumber(enrollment.getBusinessNumber())
				.ownerName(enrollment.getOwnerName())
				.marketImage(images.images().stream()
						.findFirst()
						.orElseThrow(() -> {
							throw new BusinessException(ENTITY_NOT_FOUND);
						}).imageUrl()
				)
				.build();
	}

	public static EnrollmentsComponentResponse toEnrollmentsComponentResponse(
			MarketEnrollment enrollment,
			ImageResponses images
	) {
		return EnrollmentsComponentResponse.builder()
				.enrollmentId(enrollment.getId())
				.imageUrl(images.images()
						.stream()
						.findFirst()
						.orElseThrow(() -> {
							throw new BusinessException(ENTITY_NOT_FOUND);
						}).imageUrl()
				)
				.businessNumber(enrollment.getBusinessNumber())
				.address(enrollment.getMarketAddress())
				.marketName(enrollment.getMarketName())
				.phoneNumber(enrollment.getPhoneNumber())
				.ownerName(enrollment.getOwnerName())
				.status(enrollment.getEnrollmentStatus())
				.createdAt(enrollment.getCreatedAt())
				.build();
	}

	public static EnrollmentsResponse toEnrollmentsResponse(
			List<EnrollmentsComponentResponse> enrollments,
			Long nextCursor
	) {
		return new EnrollmentsResponse(enrollments, nextCursor);
	}
}
