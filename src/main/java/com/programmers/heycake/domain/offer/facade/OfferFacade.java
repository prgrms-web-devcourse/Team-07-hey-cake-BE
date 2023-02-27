package com.programmers.heycake.domain.offer.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.offer.model.dto.request.OfferSaveRequest;
import com.programmers.heycake.domain.offer.service.OfferService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfferFacade {

	private static final String OFFER_IMAGE_SUB_PATH = "images/offers";

	private final OfferService offerService;
	private final ImageIntegrationService imageIntegrationService;

	// 임시
	private final MemberRepository memberRepository;

	@Transactional
	public Long saveOffer(OfferSaveRequest offerSaveRequest, Long memberId) {

		// TODO : 회원 검증 로직

		Long savedOfferId = offerService.saveOffer(memberId, offerSaveRequest.orderId(), offerSaveRequest.expectedPrice(),
				offerSaveRequest.content());

		imageIntegrationService.createAndUploadImage(offerSaveRequest.offerImage(), OFFER_IMAGE_SUB_PATH, savedOfferId,
				ImageType.OFFER);

		return savedOfferId;
	}

}
