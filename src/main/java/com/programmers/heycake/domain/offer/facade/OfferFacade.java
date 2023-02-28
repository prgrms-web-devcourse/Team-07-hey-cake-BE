package com.programmers.heycake.domain.offer.facade;

import static com.programmers.heycake.common.utils.JwtUtil.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.common.mapper.OfferMapper;
import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.vo.ImageType;
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
				ImageType.OFFER);

		return savedOfferId;
	}

	@Transactional(readOnly = true)
	public List<OfferSummaryResponse> getOffers(Long orderId) {
		List<OfferResponse> offerResponses = offerService.getOffersWithComments(orderId);

		return offerResponses.stream()
				.map(
						offerResponse -> {
							ImageResponse imageResponse =
									imageService.getImage(offerResponse.offerId(), ImageType.OFFER);
							MarketResponse marketResponse = marketService.getMarket(offerResponse.marketId());
							return OfferMapper.toOfferSummaryResponse(offerResponse, imageResponse, marketResponse);
						}
				)
				.toList();
	}

	@Transactional
	public void deleteOffer(Long offerId) {
		if (offerService.isReservedOffer(offerId)) {
			throw new BusinessException(ErrorCode.DELETE_ERROR);
		}
		List<String> imageUrlList = imageService.getImage(offerId, ImageType.OFFER).imageUrls();
		imageUrlList.forEach(
				imageUrl ->
						imageIntegrationService.deleteImage(offerId, ImageType.OFFER, OFFER_IMAGE_SUB_PATH, imageUrl));

		Long marketId = marketService.getMarketIdByMember(memberService.getMemberById(getMemberId()));
		offerService.deleteOffer(offerId, marketId);

		//comment service
		//offerid로 Comment 리스트 조회
		//Comment list stream 으로 삭제 메서드 호출
		// coment(db+s3) 삭제
	}
}
