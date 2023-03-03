package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.common.exception.ErrorCode.*;
import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.domain.market.mapper.MarketMapper;
import com.programmers.heycake.domain.market.model.dto.MarketResponse;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.model.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketService {

	private final MarketRepository marketRepository;
	private final MarketEnrollmentRepository marketEnrollmentRepository;

	@Transactional
	public Long enrollMarket(Long enrollmentId) {

		MarketEnrollment enrollment = getMarketEnrollment(enrollmentId);
		Member member = enrollment.getMember();

		if (member.isMarket()) {
			throw new BusinessException(FORBIDDEN);
		}
		member.changeAuthority(MARKET);

		Market market = MarketMapper.toEntity(enrollment);
		market.setMarketEnrollment(enrollment);
		market.setMember(member);
		Market savedMarket = marketRepository.save(market);

		return savedMarket.getId();
	}

	@Transactional(readOnly = true)
	public MarketResponse getMarket(Long marketId) {
		Market market = marketRepository.findByIdFetchWithMarketEnrollment(marketId)
				.orElseThrow(() -> {
					throw new BusinessException(ENTITY_NOT_FOUND);
				});
		return MarketMapper.toResponse(market);
	}

	private MarketEnrollment getMarketEnrollment(Long enrollmentId) {
		MarketEnrollment enrollment = marketEnrollmentRepository.findById(enrollmentId)
				.orElseThrow(() -> {
					throw new BusinessException(ENTITY_NOT_FOUND);
				});
		return enrollment;
	}

}
