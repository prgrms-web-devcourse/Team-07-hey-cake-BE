package com.programmers.heycake.domain.offer.facade;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.offer.model.dto.request.OfferSaveRequest;
import com.programmers.heycake.domain.offer.service.OfferService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OfferFacade {

	private static final String OFFER_IMAGE_SUB_PATH = "images/offers";

	private final OfferService offerService;
	private final ImageIntegrationService imageIntegrationService;
	private final ImageService imageService;

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

	@Transactional
	public void deleteOffer(Long offerId) {
		offerService.deleteOffer(offerId);
		String imageUrl = imageService.getImage(offerId, ImageType.OFFER).imageUrl();
		imageIntegrationService.deleteImage(offerId, ImageType.OFFER, OFFER_IMAGE_SUB_PATH, imageUrl);

		//comment service
		//offerid로 Comment 리스트 조회
		//Comment list stream 으로 삭제 메서드 호출
		// coment(db+s3) 삭제
	}
}
