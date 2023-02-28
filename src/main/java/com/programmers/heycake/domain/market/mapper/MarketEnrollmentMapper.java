package com.programmers.heycake.domain.market.mapper;

import java.util.List;

import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentControllerResponse;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentRequest;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentResponse;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentResponses;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.model.vo.MarketAddress;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarketEnrollmentMapper {

	public static MarketEnrollment toEntity(MarketEnrollmentRequest request) {
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

	public static MarketEnrollmentResponse toResponse(MarketEnrollment enrollment) {
		return MarketEnrollmentResponse.builder()
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

	public static MarketEnrollmentControllerResponse toControllerResponse(
			MarketEnrollmentResponse enrollment,
			ImageResponses images
	) {
		return MarketEnrollmentControllerResponse.builder()
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

	public static MarketEnrollmentResponses toResponse(List<MarketEnrollment> marketEnrollments) {
		List<MarketEnrollmentResponse> enrollments = marketEnrollments.stream()
				.map(MarketEnrollmentMapper::toResponse)
				.toList();
		return new MarketEnrollmentResponses(enrollments);
	}
}
