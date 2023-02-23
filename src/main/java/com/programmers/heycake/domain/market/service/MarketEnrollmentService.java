package com.programmers.heycake.domain.market.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.market.mapper.MarketEnrollmentMapper;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentRequest;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentResponse;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketEnrollmentService {

	private final MarketEnrollmentRepository marketEnrollmentRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public MarketEnrollmentResponse enrollMarket(MarketEnrollmentRequest marketEnrollmentRequest) {

		MarketEnrollment marketEnrollment = MarketEnrollmentMapper.toEntity(marketEnrollmentRequest);

		Member member = memberRepository.findById(marketEnrollmentRequest.memberId())
				.orElseThrow(() -> {
					throw new BusinessException(ErrorCode.UNAUTHORIZED);
				});
		if (member.isMarket()) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}

		marketEnrollment.setMember(member);
		MarketEnrollment savedMarketEnrollment = marketEnrollmentRepository.save(marketEnrollment);

		return new MarketEnrollmentResponse(savedMarketEnrollment.getId());
	}
}
