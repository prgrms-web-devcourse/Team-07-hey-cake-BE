package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.domain.market.mapper.MarketEnrollmentMapper;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentListRequest;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentRequest;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentResponse;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentResponses;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentCustomRepository;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketEnrollmentService {

	private final MarketEnrollmentRepository marketEnrollmentRepository;
	private final MarketEnrollmentCustomRepository marketEnrollmentCustomRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public Long enrollMarket(MarketEnrollmentRequest request) {

		MarketEnrollment enrollment = MarketEnrollmentMapper.toEntity(request);

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
	public MarketEnrollmentResponse getMarketEnrollment(Long enrollmentId) {
		MarketEnrollment enrollment = marketEnrollmentRepository.findById(enrollmentId)
				.orElseThrow(() -> {
					throw new BusinessException(ENTITY_NOT_FOUND);
				});
		return MarketEnrollmentMapper.toResponse(enrollment);
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

	public MarketEnrollmentResponses getMarketEnrollments(MarketEnrollmentListRequest request) {
		List<MarketEnrollment> marketEnrollments = marketEnrollmentCustomRepository.findAllOrderByCreatedAtDesc(
				request.cursorEnrollmentId(),
				request.pageSize(),
				request.status()
		);
		return MarketEnrollmentMapper.toResponse(marketEnrollments);
	}
}
