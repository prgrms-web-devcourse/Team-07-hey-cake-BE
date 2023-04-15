package com.programmers.heycake.domain.facade;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.market.mapper.MarketMapper;
import com.programmers.heycake.domain.market.model.dto.response.MarketResponse;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.service.FollowService;
import com.programmers.heycake.domain.market.service.MarketService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketFacade {

	private final MarketService marketService;
	private final ImageService imageService;
	private final FollowService followService;

	@Transactional(readOnly = true)
	public MarketResponse getMarket(Long marketId) {
		Market market = marketService.getMarketWithMarketEnrollmentById(marketId);
		ImageResponses images = imageService.getImages(marketId, ImageType.MARKET);
		Long followNumber = followService.getFollowNumber(marketId);
		if (!isAnonymous()) {
			boolean followed = followService.isFollowed(getMemberId(), marketId);
			return MarketMapper.toMarketResponse(market, images, followNumber, followed);
		}
		return MarketMapper.toMarketResponse(market, images, followNumber);
	}
}
