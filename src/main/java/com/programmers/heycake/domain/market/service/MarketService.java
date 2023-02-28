package com.programmers.heycake.domain.market.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.market.mapper.MarketMapper;
import com.programmers.heycake.domain.market.model.dto.MarketResponse;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.model.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketService {

	private final MarketRepository marketRepository;

	@Transactional(readOnly = true)
	public MarketResponse getMarket(Long marketId) {
		Market market = marketRepository.findByIdFetchWithMarketEnrollment(marketId)
				.orElseThrow(() -> {
					throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
				});
		return MarketMapper.toControllerResponse(market);
	}

	@Transactional(readOnly = true)
	public Long getMarketIdByMember(Member member) {
		return marketRepository.findByMember(member)
				.orElseThrow(
						() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
				).getId();
	}

}
