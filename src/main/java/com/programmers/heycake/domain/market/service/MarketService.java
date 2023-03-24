package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.common.exception.ErrorCode.*;
import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.mapper.MarketMapper;
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
	public Long createMarket(Long enrollmentId) {

		MarketEnrollment enrollment = getMarketEnrollment(enrollmentId);
		Member member = enrollment.getMember();

		if (member.isMarket()) {
			throw new BusinessException(DUPLICATED);
		}
		member.changeAuthority(MARKET);

		Market market = MarketMapper.toEntity(enrollment);
		market.setMarketEnrollment(enrollment);
		market.setMember(member);
		Market savedMarket = marketRepository.save(market);
		return savedMarket.getId();
	}

	@Transactional(readOnly = true)
	public Market getMarketWithMarketEnrollmentById(Long marketId) {
		return marketRepository.findFetchWithMarketEnrollmentById(marketId)
				.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public Market getMarketById(Long marketId) {
		return marketRepository.findById(marketId)
				.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public Long getMarketIdByMember(Member member) {
		return marketRepository.findByMember(member)
				.orElseThrow(
						() -> new BusinessException(ENTITY_NOT_FOUND)
				).getId();
	}

	@Transactional(readOnly = true)
	public Market getMarketByMember(Member member) {
		return marketRepository.findByMember(member)
				.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND));
	}

	private MarketEnrollment getMarketEnrollment(Long enrollmentId) {
		return marketEnrollmentRepository.findById(enrollmentId)
				.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND));
	}
}
