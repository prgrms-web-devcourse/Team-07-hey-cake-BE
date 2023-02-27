package com.programmers.heycake.domain.market.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.market.mapper.MarketMapper;
import com.programmers.heycake.domain.market.model.dto.MarketControllerResponse;
import com.programmers.heycake.domain.market.model.dto.MarketResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketFacade {

	private final MarketService marketService;
	private final ImageService imageService;

	@Transactional(readOnly = true)
	public MarketControllerResponse getMarket(Long marketId) {
		MarketResponse market = marketService.getMarket(marketId);
		ImageResponse image = imageService.getImage(marketId, ImageType.MARKET);
		return MarketMapper.toControllerResponse(market, image);
	}
}
