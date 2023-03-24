package com.programmers.heycake.domain.offer.facade;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.mapper.OfferMapper;
import com.programmers.heycake.domain.comment.facade.CommentFacade;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.service.MarketService;
import com.programmers.heycake.domain.member.service.MemberService;
import com.programmers.heycake.domain.offer.model.dto.request.OfferSaveRequest;
import com.programmers.heycake.domain.offer.model.dto.response.OfferResponse;
import com.programmers.heycake.domain.offer.model.dto.response.OfferSummaryResponse;
import com.programmers.heycake.domain.offer.service.OfferService;
import com.programmers.heycake.domain.order.service.HistoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfferFacade {

	private static final String OFFER_IMAGE_SUB_PATH = "images/offers";

	private final OfferService offerService;
	private final MarketService marketService;
	private final MemberService memberService;
	private final ImageService imageService;
	private final CommentFacade commentFacade;
	private final HistoryService historyService;
	private final ImageIntegrationService imageIntegrationService;

	@Transactional
	public Long saveOffer(OfferSaveRequest offerSaveRequest) {

		Long savedOfferId = offerService.saveOffer(offerSaveRequest.orderId(), offerSaveRequest.expectedPrice(),
				offerSaveRequest.content());

		imageIntegrationService.createAndUploadImage(offerSaveRequest.offerImage(), OFFER_IMAGE_SUB_PATH, savedOfferId,
				OFFER);

		return savedOfferId;
	}

	@Transactional
	public void deleteOffer(Long offerId) {
		Long marketId = marketService.getMarketIdByMember(memberService.getMemberById(getMemberId()));
		imageIntegrationService.deleteImages(offerId, OFFER, OFFER_IMAGE_SUB_PATH);

		List<Long> offerCommentIdList = offerService.getOfferCommentIdList(offerId);
		offerCommentIdList.forEach(commentFacade::deleteCommentWithoutAuth);

		offerService.deleteOffer(offerId, marketId);
	}

	@Transactional
	public void deleteOfferWithoutAuth(Long offerId) {
		imageIntegrationService.deleteImages(offerId, OFFER, OFFER_IMAGE_SUB_PATH);

		List<Long> offerCommentIdList = offerService.getOfferCommentIdList(offerId);
		offerCommentIdList.forEach(commentFacade::deleteCommentWithoutAuth);

		offerService.deleteOfferWithoutAuth(offerId);
	}

	@Transactional(readOnly = true)
	public List<OfferSummaryResponse> getOffers(Long orderId) {
		List<OfferResponse> offerResponses = offerService.getOffersWithComments(orderId);

		return offerResponses.stream()
				.map(
						offerResponse -> {
							ImageResponses imageResponses = imageService.getImages(offerResponse.offerId(), OFFER);
							Market market = marketService.getMarket(offerResponse.marketId());
							boolean isPaid = historyService.isPaidOffer(offerResponse.marketId(), orderId);
							return OfferMapper.toOfferSummaryResponse(offerResponse, imageResponses, market, isPaid);
						}
				)
				.toList();
	}
}
