package com.programmers.heycake.domain.offer.facade;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.mapper.OfferMapper;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.market.model.dto.MarketResponse;
import com.programmers.heycake.domain.market.service.MarketService;
import com.programmers.heycake.domain.offer.model.dto.request.OfferSaveRequest;
import com.programmers.heycake.domain.offer.model.dto.response.OfferResponse;
import com.programmers.heycake.domain.offer.model.dto.response.OfferSummaryResponse;
import com.programmers.heycake.domain.offer.service.OfferService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfferFacade {

	private static final String OFFER_IMAGE_SUB_PATH = "images/offers";

	private final OfferService offerService;
	private final ImageService imageService;
	private final ImageIntegrationService imageIntegrationService;
	private final MarketService marketService;

	@Transactional
	public Long saveOffer(OfferSaveRequest offerSaveRequest, Long memberId) {

		// TODO : 회원 검증 로직

		Long savedOfferId = offerService.saveOffer(memberId, offerSaveRequest.orderId(), offerSaveRequest.expectedPrice(),
				offerSaveRequest.content());

		imageIntegrationService.createAndUploadImage(offerSaveRequest.offerImage(), OFFER_IMAGE_SUB_PATH, savedOfferId,
				ImageType.OFFER);

		return savedOfferId;
	}

	@Transactional(readOnly = true)
	public List<OfferSummaryResponse> getOffers(Long orderId) {
		List<OfferResponse> offerResponses = offerService.getOffersWithComments(orderId);

		return offerResponses.stream()
				.map(
						offerResponse -> {
							ImageResponses imageResponses = imageService.getImages(offerResponse.offerId(), ImageType.OFFER);
							MarketResponse marketResponse = marketService.getMarket(offerResponse.marketId());
							return OfferMapper.toOfferSummaryResponse(offerResponse, imageResponses, marketResponse);
						}
				)
				.toList();
	}

}