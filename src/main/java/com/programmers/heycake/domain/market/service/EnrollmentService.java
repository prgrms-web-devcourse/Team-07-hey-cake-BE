package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.common.exception.ErrorCode.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.repository.ImageRepository;
import com.programmers.heycake.domain.market.mapper.EnrollmentMapper;
import com.programmers.heycake.domain.market.model.dto.EnrollmentListDetailResponse;
import com.programmers.heycake.domain.market.model.dto.EnrollmentListRequest;
import com.programmers.heycake.domain.market.model.dto.EnrollmentListResponse;
import com.programmers.heycake.domain.market.model.dto.EnrollmentRequest;
import com.programmers.heycake.domain.market.model.dto.EnrollmentResponse;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;
import com.programmers.heycake.domain.market.repository.EnrollmentQueryDslRepository;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

	private final MarketEnrollmentRepository marketEnrollmentRepository;
	private final EnrollmentQueryDslRepository enrollmentQueryDslRepository;
	private final MemberRepository memberRepository;
	private final ImageRepository imageRepository;

	@Transactional
	public Long enrollMarket(EnrollmentRequest request) {

		MarketEnrollment enrollment = EnrollmentMapper.toEntity(request);

		// todo 인증 완성 시 회원 조회 방식 변경
		Member member = memberRepository.findById(request.memberId())
				.orElseThrow(() -> {
					throw new BusinessException(UNAUTHORIZED);
				});
		if (member.isMarket()) {
			throw new BusinessException(FORBIDDEN);
		}

		enrollment.setMember(member);
		MarketEnrollment savedEnrollment = marketEnrollmentRepository.save(enrollment);

		return savedEnrollment.getId();
	}

	@Transactional(readOnly = true)
	public EnrollmentResponse getMarketEnrollment(Long enrollmentId) {
		MarketEnrollment enrollment = marketEnrollmentRepository.findById(enrollmentId)
				.orElseThrow(() -> {
					throw new BusinessException(ENTITY_NOT_FOUND);
				});
		return EnrollmentMapper.toResponse(enrollment);
	}

	@Transactional
	public void changeEnrollmentStatus(Long enrollmentId, EnrollmentStatus status) {
		MarketEnrollment enrollment = marketEnrollmentRepository.findByIdFetchWithMember(enrollmentId)
				.orElseThrow(() -> {
					throw new BusinessException(ENTITY_NOT_FOUND);
				});

		if (enrollment.isSameStatus(status)) {
			throw new BusinessException(DUPLICATED);
		}

		enrollment.updateEnrollmentStatus(status);
	}

	public EnrollmentListResponse getMarketEnrollments(EnrollmentListRequest request) {
		List<MarketEnrollment> marketEnrollments = enrollmentQueryDslRepository.findAllOrderByCreatedAtDesc(
				request.cursorEnrollmentId(),
				request.pageSize(),
				request.status()
		);
		List<EnrollmentListDetailResponse> enrollmentResponses = marketEnrollments.stream()
				.map(enrollment -> {
					List<Image> images = imageRepository.findAllByReferenceIdAndImageType(enrollment.getId(), ENROLLMENT_MARKET);
					return EnrollmentMapper.toResponse(enrollment, images);
				})
				.toList();

		Long nextCursor =
				marketEnrollments.size() < request.pageSize() ?
						0 : marketEnrollments.get(marketEnrollments.size() - 1).getId();

		return EnrollmentMapper.toResponse(enrollmentResponses, nextCursor);
	}
}
