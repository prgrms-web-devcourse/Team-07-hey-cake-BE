package com.programmers.heycake.common.mapper;

import static com.programmers.heycake.common.exception.ErrorCode.*;

import java.util.List;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentCreateRequest;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentDetailNoImageResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentDetailWithImageResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentGetListResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentListSummaryNoImageResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentListSummaryWithImageResponse;
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

	public static EnrollmentDetailNoImageResponse toEnrollmentDetailNoImageResponse(MarketEnrollment enrollment) {
		return EnrollmentDetailNoImageResponse.builder()
				.enrollmentId(enrollment.getId())
				.phoneNumber(enrollment.getPhoneNumber())
				.marketAddress(enrollment.getMarketAddress())
				.openTime(enrollment.getOpenTime())
				.endTime(enrollment.getEndTime())
				.description(enrollment.getDescription())
				.marketName(enrollment.getMarketName())
				.businessNumber(enrollment.getBusinessNumber())
				.ownerName(enrollment.getOwnerName())
				.build();
	}

	public static EnrollmentListSummaryNoImageResponse toEnrollmentListSummaryNoImageResponse(
			MarketEnrollment enrollment) {
		return EnrollmentListSummaryNoImageResponse.builder()
				.enrollmentId(enrollment.getId())
				.businessNumber(enrollment.getBusinessNumber())
				.address(enrollment.getMarketAddress())
				.marketName(enrollment.getMarketName())
				.phoneNumber(enrollment.getPhoneNumber())
				.ownerName(enrollment.getOwnerName())
				.status(enrollment.getEnrollmentStatus())
				.createdAt(enrollment.getCreatedAt())
				.build();
	}

	public static EnrollmentDetailWithImageResponse toEnrollmentDetailWithImageResponse(
			EnrollmentDetailNoImageResponse enrollment,
			ImageResponses images
	) {
		return EnrollmentDetailWithImageResponse.builder()
				.phoneNumber(enrollment.phoneNumber())
				.marketAddress(enrollment.marketAddress())
				.openTime(enrollment.openTime())
				.endTime(enrollment.endTime())
				.description(enrollment.description())
				.marketName(enrollment.marketName())
				.businessNumber(enrollment.businessNumber())
				.ownerName(enrollment.ownerName())
				.marketImage(images.images().stream()
						.findFirst()
						.orElseThrow(() -> {
							throw new BusinessException(ENTITY_NOT_FOUND);
						}).imageUrl()
				)
				.build();
	}

	public static EnrollmentListSummaryWithImageResponse toEnrollmentListSummaryWithImageResponse(
			EnrollmentListSummaryNoImageResponse enrollment,
			ImageResponses images
	) {
		return EnrollmentListSummaryWithImageResponse.builder()
				.enrollmentId(enrollment.enrollmentId())
				.imageUrl(images.images()
						.stream()
						.findFirst()
						.orElseThrow(() -> {
							throw new BusinessException(ENTITY_NOT_FOUND);
						}).imageUrl()
				)
				.businessNumber(enrollment.businessNumber())
				.address(enrollment.address())
				.marketName(enrollment.marketName())
				.phoneNumber(enrollment.phoneNumber())
				.ownerName(enrollment.ownerName())
				.status(enrollment.status())
				.createdAt(enrollment.createdAt())
				.build();
	}

	public static EnrollmentGetListResponse toEnrollmentGetListResponse(
			List<EnrollmentListSummaryWithImageResponse> enrollments,
			Long nextCursor) {
		return new EnrollmentGetListResponse(enrollments, nextCursor);
	}
}
