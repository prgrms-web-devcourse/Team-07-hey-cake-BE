package com.programmers.heycake.domain.offer.facade;

import static com.programmers.heycake.common.utils.JwtUtil.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.mapper.OfferMapper;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.market.model.dto.MarketResponse;
import com.programmers.heycake.domain.market.service.MarketService;
import com.programmers.heycake.domain.member.service.MemberService;
import com.programmers.heycake.domain.offer.model.dto.request.OfferSaveRequest;
import com.programmers.heycake.domain.offer.model.dto.response.OfferResponse;
import com.programmers.heycake.domain.offer.model.dto.response.OfferSummaryResponse;
import com.programmers.heycake.domain.offer.service.OfferService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OfferFacade {

	private static final String OFFER_IMAGE_SUB_PATH = "images/offers";

	private final OfferService offerService;
	private final ImageService imageService;
	private final ImageIntegrationService imageIntegrationService;
	private final MarketService marketService;

	private final MemberService memberService;

	// 임시
	// private final MemberRepository memberRepository;
	// private final CommentService commentService;

	@Transactional
	public Long saveOffer(OfferSaveRequest offerSaveRequest, Long memberId) {

		// TODO : 회원 검증 로직

		Long savedOfferId = offerService.saveOffer(
				memberId,
				offerSaveRequest.orderId(),
				offerSaveRequest.expectedPrice(),
				offerSaveRequest.content()
		);

		imageIntegrationService.createAndUploadImage(offerSaveRequest.offerImage(), OFFER_IMAGE_SUB_PATH, savedOfferId,
				OFFER);

		return savedOfferId;
	}

	@Transactional(readOnly = true)
	public List<OfferSummaryResponse> getOffers(Long orderId) {
		List<OfferResponse> offerResponses = offerService.getOffersWithComments(orderId);

		return offerResponses.stream()
				.map(
						offerResponse -> {
							ImageResponses imageResponses = imageService.getImages(offerResponse.offerId(), OFFER);
							MarketResponse marketResponse = marketService.getMarket(offerResponse.marketId());
							return OfferMapper.toOfferSummaryResponse(offerResponse, imageResponses, marketResponse);
						}
				)
				.toList();
	}

	@Transactional
	public void deleteOffer(Long offerId) {
		ImageResponses images = imageService.getImages(offerId, OFFER);
		images.images()
				.forEach(
						image -> imageIntegrationService.deleteImage(
								offerId, OFFER, OFFER_IMAGE_SUB_PATH, image.savedFilename()));

		//TODO 엔티티 가져다 쓰는거 뭔가 마음엠 안듦
		Long marketId = marketService.getMarketIdByMember(memberService.getMemberById(getMemberId()));
		offerService.deleteOffer(offerId, marketId);

		//comment service
		//offerid로 Comment 리스트 조회
		//Comment list stream 으로 삭제 메서드 호출
		// coment(db+s3) 삭제
	}

	@Transactional
	public void deleteOfferWithoutAuth(Long offerId) {
		ImageResponses images = imageService.getImages(offerId, OFFER);
		images.images()
				.forEach(image -> imageIntegrationService.deleteImage(
						offerId, OFFER, OFFER_IMAGE_SUB_PATH, image.savedFilename()));

		offerService.deleteOfferWithoutAuth(offerId);
		//comment service
		//offerid로 Comment 리스트 조회
		//Comment list stream 으로 삭제 메서드 호출
		// coment(db+s3) 삭제
	}
}
