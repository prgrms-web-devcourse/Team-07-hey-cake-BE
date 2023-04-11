package com.programmers.heycake.domain.facade;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;
import static com.programmers.heycake.domain.market.mapper.MarketMapper.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.market.model.dto.request.FollowMarketRequest;
import com.programmers.heycake.domain.market.model.dto.response.FollowMarketsResponse;
import com.programmers.heycake.domain.market.model.dto.response.MarketDetailResponse;
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
	private final ImageService imageService;

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

	@Transactional(readOnly = true)
	public FollowMarketsResponse getFollowMarkets(FollowMarketRequest followMarketRequest) {
		List<Long> followMarketIds = followService.getFollowMarketIds(followMarketRequest, getMemberId());

		List<MarketDetailResponse> myFollowMarkets = followMarketIds.stream()
				.map(id -> toMarketDetailResponse(
						marketService.getMarketById(id),
						imageService.getImages(id, ImageType.MARKET)
				))
				.toList();

		Long lastId = followMarketIds.isEmpty() ? 0L : followMarketIds.get(followMarketIds.size() - 1);

		return toFollowMarketsResponse(myFollowMarkets, lastId);
	}
}
