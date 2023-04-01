package com.programmers.heycake.domain.facade;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.common.mapper.OfferMapper;
import com.programmers.heycake.domain.comment.facade.CommentFacade;
import com.programmers.heycake.domain.comment.service.CommentService;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.service.MarketService;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.service.MemberService;
import com.programmers.heycake.domain.offer.model.dto.request.OfferCreateRequest;
import com.programmers.heycake.domain.offer.model.dto.response.OffersResponse;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.service.OfferService;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.service.HistoryService;
import com.programmers.heycake.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfferFacade {

	private static final String OFFER_IMAGE_SUB_PATH = "images/offers";

	private final CommentFacade commentFacade;
	private final CommentService commentService;
	private final OfferService offerService;
	private final OrderService orderService;
	private final MarketService marketService;
	private final MemberService memberService;
	private final HistoryService historyService;
	private final ImageService imageService;

	@Transactional
	public Long createOffer(OfferCreateRequest offerCreateRequest) {
		Order order = orderService.getOrderById(offerCreateRequest.orderId());
		Member member = memberService.getMemberById(getMemberId());
		Market market = marketService.getMarketByMember(member);

		Long createdOfferId = offerService.createOffer(
				order,
				market,
				offerCreateRequest.expectedPrice(),
				offerCreateRequest.content()
		);

		imageService.createAndUploadImage(
				offerCreateRequest.offerImage(),
				OFFER_IMAGE_SUB_PATH,
				createdOfferId,
				OFFER
		);

		return createdOfferId;
	}

	@Transactional
	public void deleteOffer(Long offerId) {
		Long marketId = marketService.getMarketIdByMember(memberService.getMemberById(getMemberId()));
		imageService.deleteImages(offerId, OFFER);

		List<Long> offerCommentIdList = offerService.getOfferCommentIdList(offerId);
		offerCommentIdList.forEach(commentFacade::deleteCommentWithoutAuth);

		offerService.deleteOffer(offerId, marketId);
	}

	@Transactional
	public void deleteOfferWithoutAuth(Long offerId) {
		imageService.deleteImages(offerId, OFFER);

		List<Long> offerCommentIdList = offerService.getOfferCommentIdList(offerId);
		offerCommentIdList.forEach(commentFacade::deleteCommentWithoutAuth);

		offerService.deleteOfferWithoutAuth(offerId);
	}

	@Transactional(readOnly = true)
	public List<OffersResponse> getOffers(Long orderId) {
		validateOrderExistsByOrderId(orderId);

		List<Offer> offers = offerService.getOffersByOrderId(orderId);
		return offers.stream()
				.map(
						offer -> {
							Market market = marketService.getMarketWithMarketEnrollmentById(offer.getMarketId());
							ImageResponses imageResponses = imageService.getImages(offer.getId(), OFFER);
							boolean isPaid = historyService.isPaidOffer(offer.getMarketId(), orderId);
							int numberOfCommentsInOffer = commentService.countCommentsByOffer(offer);
							return OfferMapper.toOffersResponse(offer, market, imageResponses, isPaid, numberOfCommentsInOffer);
						}
				)
				.toList();
	}

	private void validateOrderExistsByOrderId(Long orderId) {
		if (!orderService.existsById(orderId)) {
			throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
		}
	}
}
