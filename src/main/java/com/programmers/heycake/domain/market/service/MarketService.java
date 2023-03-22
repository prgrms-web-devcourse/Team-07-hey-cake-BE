package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.common.exception.ErrorCode.*;
import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.mapper.MarketMapper;
import com.programmers.heycake.domain.market.model.dto.response.MarketDetailNoImageResponse;
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
	public MarketDetailNoImageResponse getMarket(Long marketId) {
		Market market = marketRepository.findByIdFetchWithMarketEnrollment(marketId)
				.orElseThrow(() -> {
					throw new BusinessException(ENTITY_NOT_FOUND);
				});
		return MarketMapper.toMarketDetailNoImageResponse(market);
	}

	@Transactional(readOnly = true)
	public Long getMarketIdByMember(Member member) {
		return marketRepository.findByMember(member)
				.orElseThrow(
						() -> new BusinessException(ENTITY_NOT_FOUND)
				).getId();
	}

	private MarketEnrollment getMarketEnrollment(Long enrollmentId) {
		MarketEnrollment enrollment = marketEnrollmentRepository.findById(enrollmentId)
				.orElseThrow(() -> {
					throw new BusinessException(ENTITY_NOT_FOUND);
				});
		return enrollment;
	}

	public Market getMarketById(Long marketId) {
		return marketRepository.findById(marketId)
				.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND));
	}
}
