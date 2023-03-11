package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.common.exception.ErrorCode.*;
import static com.programmers.heycake.common.util.AuthenticationUtil.*;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.mapper.EnrollmentMapper;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentCreateRequest;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentGetListRequest;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentDetailNoImageResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentListSummaryNoImageResponse;
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

	@Transactional
	public Long createEnrollment(EnrollmentCreateRequest request) {

		MarketEnrollment enrollment = EnrollmentMapper.toEntity(request);

		if (enrollment.hasOpenDateAfterNow()) {
			throw new BusinessException(BAD_REQUEST);
		}

		Member member = memberRepository.findById(getMemberId())
				.orElseThrow(() -> {
					throw new BusinessException(ENTITY_NOT_FOUND);
				});
		if (member.isMarket()) {
			throw new AccessDeniedException("이미 업체인 고객입니다.");
		}

		enrollment.setMember(member);
		MarketEnrollment savedEnrollment = marketEnrollmentRepository.save(enrollment);

		return savedEnrollment.getId();
	}

	@Transactional(readOnly = true)
	public EnrollmentDetailNoImageResponse getMarketEnrollment(Long enrollmentId) {
		MarketEnrollment enrollment = marketEnrollmentRepository.findById(enrollmentId)
				.orElseThrow(() -> {
					throw new BusinessException(ENTITY_NOT_FOUND);
				});
		return EnrollmentMapper.toEnrollmentDetailNoImageResponse(enrollment);
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

	public List<EnrollmentListSummaryNoImageResponse> getMarketEnrollments(EnrollmentGetListRequest request) {
		List<MarketEnrollment> marketEnrollments = enrollmentQueryDslRepository.findAllOrderByCreatedAtDesc(
				request.cursorEnrollmentId(),
				request.pageSize(),
				request.status()
		);

		return marketEnrollments.stream()
				.map(EnrollmentMapper::toEnrollmentListSummaryNoImageResponse)
				.toList();
	}
}
