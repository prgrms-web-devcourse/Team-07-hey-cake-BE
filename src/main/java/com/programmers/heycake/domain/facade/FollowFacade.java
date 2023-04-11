package com.programmers.heycake.domain.facade;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.market.service.FollowService;
import com.programmers.heycake.domain.market.service.MarketService;
import com.programmers.heycake.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FollowFacade {
	private final FollowService followService;
	private final MarketService marketService;
	private final MemberService memberService;

	@Transactional
	public Long createFollow(Long marketId) {
		marketService.checkMarketExists(marketId);

		Long memberId = getMemberId();
		if (memberService.isMarketById(memberId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}

		return followService.createFollow(marketId, memberId);
	}

	@Transactional
	public void deleteFollow(Long marketId) {
		marketService.checkMarketExists(marketId);
		followService.deleteFollow(marketId);
	}

}
