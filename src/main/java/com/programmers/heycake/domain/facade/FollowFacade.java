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
import com.programmers.heycake.domain.market.model.dto.response.FollowedMarketsResponse;
import com.programmers.heycake.domain.market.model.dto.response.MarketResponse;
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
	public FollowedMarketsResponse getFollowedMarkets(FollowMarketRequest followMarketRequest) {
		List<Long> followedMarketIds = followService.getFollowedMarketIds(followMarketRequest, getMemberId());

		List<MarketResponse> followedMarkets = followedMarketIds.stream()
				.map(id -> toMarketResponse(
						marketService.getMarketById(id),
						imageService.getImages(id, ImageType.MARKET),
						followService.getFollowedCount(id)
				))
				.toList();

		Long lastId = followedMarketIds.isEmpty() ? 0L : followedMarketIds.get(followedMarketIds.size() - 1);

		return toFollowMarketsResponse(followedMarkets, lastId);
	}
}
