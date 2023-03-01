package com.programmers.heycake.domain.market.mapper;

import java.util.List;

import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.market.model.dto.EnrollmentControllerResponse;
import com.programmers.heycake.domain.market.model.dto.EnrollmentListInfoResponse;
import com.programmers.heycake.domain.market.model.dto.EnrollmentListResponse;
import com.programmers.heycake.domain.market.model.dto.EnrollmentRequest;
import com.programmers.heycake.domain.market.model.dto.EnrollmentResponse;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.model.vo.MarketAddress;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrollmentMapper {

	public static MarketEnrollment toEntity(EnrollmentRequest request) {
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

	public static EnrollmentResponse toResponse(MarketEnrollment enrollment) {
		return EnrollmentResponse.builder()
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

	public static EnrollmentControllerResponse toControllerResponse(
			EnrollmentResponse enrollment,
			ImageResponses images
	) {
		return EnrollmentControllerResponse.builder()
				.phoneNumber(enrollment.phoneNumber())
				.marketAddress(enrollment.marketAddress())
				.openTime(enrollment.openTime())
				.endTime(enrollment.endTime())
				.description(enrollment.description())
				.marketName(enrollment.marketName())
				.businessNumber(enrollment.businessNumber())
				.ownerName(enrollment.ownerName())
				.marketImage(images.images().get(0).imageUrls())
				.build();
	}

	public static EnrollmentListInfoResponse toResponse(MarketEnrollment enrollment, List<Image> images) {
		return EnrollmentListInfoResponse.builder()
				.enrollmentId(enrollment.getId())
				.imageUrl(images.get(0).getImageUrl())
				.businessNumber(enrollment.getBusinessNumber())
				.address(enrollment.getMarketAddress())
				.marketName(enrollment.getMarketName())
				.phoneNumber(enrollment.getPhoneNumber())
				.ownerName(enrollment.getOwnerName())
				.status(enrollment.getEnrollmentStatus())
				.createdAt(enrollment.getCreatedAt())
				.build();
	}

	public static EnrollmentListResponse toResponse(List<EnrollmentListInfoResponse> enrollments, Long nextCursor) {
		return new EnrollmentListResponse(enrollments, nextCursor);
	}
}
