package com.programmers.heycake.domain.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.mapper.MarketMapper;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.market.model.dto.response.MarketDetailResponse;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.service.MarketService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketFacade {

	private final MarketService marketService;
	private final ImageService imageService;

	@Transactional(readOnly = true)
	public MarketDetailResponse getMarket(Long marketId) {
		Market market = marketService.getMarket(marketId);
		ImageResponses images = imageService.getImages(marketId, ImageType.MARKET);
		return MarketMapper.toMarketDetailResponse(market, images);
	}
}
