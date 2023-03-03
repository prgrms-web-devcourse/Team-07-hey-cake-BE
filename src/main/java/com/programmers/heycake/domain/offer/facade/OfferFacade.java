package com.programmers.heycake.domain.offer.facade;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.mapper.OfferMapper;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.market.model.dto.response.MarketDetailNoImageResponse;
import com.programmers.heycake.domain.market.service.MarketService;
import com.programmers.heycake.domain.member.service.MemberService;
import com.programmers.heycake.domain.offer.model.dto.request.OfferSaveRequest;
import com.programmers.heycake.domain.offer.model.dto.request.OfferSummaryRequest;
import com.programmers.heycake.domain.offer.model.dto.response.OfferResponse;
import com.programmers.heycake.domain.offer.model.dto.response.OfferSummaryResponse;
import com.programmers.heycake.domain.offer.service.OfferService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfferFacade {

	private static final String OFFER_IMAGE_SUB_PATH = "images/offers";

	private final OfferService offerService;
	private final MarketService marketService;
	private final MemberService memberService;
	private final ImageService imageService;
	private final ImageIntegrationService imageIntegrationService;

	@Transactional
	public Long saveOffer(OfferSaveRequest offerSaveRequest, Long memberId) {

		// TODO : 회원 검증 로직

		Long savedOfferId = offerService.saveOffer(memberId, offerSaveRequest.orderId(), offerSaveRequest.expectedPrice(),
				offerSaveRequest.content());

		imageIntegrationService.createAndUploadImage(offerSaveRequest.offerImage(), OFFER_IMAGE_SUB_PATH, savedOfferId,
				OFFER);

		return savedOfferId;
	}

	@Transactional
	public void deleteOffer(Long offerId) {
		Long marketId = marketService.getMarketIdByMember(memberService.getMemberById(getMemberId()));
		offerService.deleteOffer(offerId, marketId);
		imageIntegrationService.deleteImages(offerId, OFFER, OFFER_IMAGE_SUB_PATH);

		//comment service
		//offerid로 Comment 리스트 조회
		//Comment list stream 으로 삭제 메서드 호출
		// coment(db+s3) 삭제
	}

	@Transactional(readOnly = true)
	public List<OfferSummaryResponse> getOffers(OfferSummaryRequest offerSummaryRequest) {
		List<OfferResponse> offerResponses = offerService.getOffersWithComments(offerSummaryRequest.orderId());

		return offerResponses.stream()
				.map(
						offerResponse -> {
							ImageResponses imageResponses = imageService.getImages(offerResponse.offerId(), ImageType.OFFER);
							MarketDetailNoImageResponse marketResponse = marketService.getMarket(offerResponse.marketId());
							return OfferMapper.toOfferSummaryResponse(offerResponse, imageResponses, marketResponse);
						}
				)
				.toList();
	}

}
