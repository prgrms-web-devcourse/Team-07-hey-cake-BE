package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.common.exception.ErrorCode.*;
import static com.programmers.heycake.common.util.AuthenticationUtil.*;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.domain.market.mapper.EnrollmentMapper;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentCreateRequest;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentsRequest;
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
				.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND));
		if (member.isMarket()) {
			throw new AccessDeniedException("이미 업체인 고객입니다.");
		}

		enrollment.setMember(member);
		MarketEnrollment savedEnrollment = marketEnrollmentRepository.save(enrollment);

		return savedEnrollment.getId();
	}

	@Transactional(readOnly = true)
	public MarketEnrollment getMarketEnrollment(Long enrollmentId) {
		return marketEnrollmentRepository.findById(enrollmentId)
				.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND));
	}

	@Transactional
	public void updateEnrollmentStatus(Long enrollmentId, EnrollmentStatus status) {
		MarketEnrollment enrollment = marketEnrollmentRepository.findByIdFetchWithMember(enrollmentId)
				.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND));

		if (enrollment.isSameStatus(status)) {
			throw new BusinessException(DUPLICATED);
		}

		enrollment.updateEnrollmentStatus(status);
	}

	public List<MarketEnrollment> getMarketEnrollments(EnrollmentsRequest request) {
		return enrollmentQueryDslRepository.findAllOrderByCreatedAtDesc(
				request.cursorEnrollmentId(),
				request.pageSize(),
				request.status()
		);
	}
}
