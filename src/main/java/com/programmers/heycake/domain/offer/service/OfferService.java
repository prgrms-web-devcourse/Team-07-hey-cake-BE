package com.programmers.heycake.domain.offer.service;

import static com.programmers.heycake.common.mapper.OfferMapper.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.common.mapper.OfferMapper;
import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.offer.model.dto.OfferDto;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.model.entity.Order;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfferService {

	private final OfferRepository offerRepository;

	public Long createOffer(Order order, Market market, int expectedPrice, String content) {
		validateOrderIsExpired(order);
		validateVisitDatePassed(order);
		validateDuplicateOffer(market.getId(), order);

		Offer offer = OfferMapper.toEntity(market.getId(), expectedPrice, content);
		offer.setOrder(order);

		offerRepository.save(offer);

		return offer.getId();
	}

	@Transactional
	public void deleteOffer(Long offerId, Long marketId) {
		identifyAuthor(offerId, marketId);
		validateIsNew(offerId);
		offerRepository.deleteById(offerId);
	}

	@Transactional(readOnly = true)
	public List<Long> getOffersCommentId(Long offerId) {
		return getOffer(offerId)
				.getComments()
				.stream()
				.map(Comment::getId)
				.toList();
	}

	@Transactional(readOnly = true)
	public OfferDto getOfferById(Long offerId) {
		return toOfferDto(getOffer(offerId));
	}

	@Transactional
	public void deleteOfferWithoutAuth(Long offerId) {
		offerRepository.deleteById(offerId);
	}

	private Offer getOffer(Long offerId) {
		return offerRepository
				.findById(offerId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

	private void identifyAuthor(Long offerId, Long marketId) {
		if (!getOffer(offerId).identifyAuthor(marketId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
	}

	private void validateIsNew(Long offerId) {
		if (getOffer(offerId).getOrder().isExpired()) {
			throw new BusinessException(ErrorCode.ORDER_EXPIRED);
		}
	}

	private void validateDuplicateOffer(Long marketId, Order order) {
		if (offerRepository.existsByMarketIdAndOrder(marketId, order)) {
			throw new BusinessException(ErrorCode.DUPLICATED_OFFER);
		}
	}

	private void validateVisitDatePassed(Order order) {
		if (order.isPassedVisitDate(LocalDateTime.now())) {
			throw new BusinessException(ErrorCode.VISIT_DATE_PASSED);
		}
	}

	private void validateOrderIsExpired(Order order) {
		if (order.isExpired()) {
			throw new BusinessException(ErrorCode.ORDER_EXPIRED);
		}
	}

	public List<Offer> getOffersByOrderId(Long orderId) {
		return offerRepository.findAllByOrderId(orderId)
				.stream()
				.toList();
	}

	public Offer getOfferWithOrderById(Long offerId) {
		return offerRepository.findFetchWithOrderById(offerId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}
}
