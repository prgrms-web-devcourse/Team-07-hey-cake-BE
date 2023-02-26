package com.programmers.heycake.domain.offer.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.common.mapper.OfferMapper;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfferService {

	private final OfferRepository offerRepository;
	private final MemberRepository memberRepository;
	private final OrderRepository orderRepository;
	private final MarketRepository marketRepository;

	public Long saveOffer(Long memberId, Long orderId, int expectedPrice, String content) {

		Order order = getOrder(orderId);
		Member member = getMember(memberId);
		Market market = getMarket(member);

		validateSaveOffer(order, market);

		Offer offer = OfferMapper.toEntity(market.getId(), expectedPrice, content);
		offer.setOrder(order);

		offerRepository.save(offer);

		return offer.getId();
	}

	private void validateSaveOffer(Order order, Market market) {
		if (offerRepository.existsByMarketIdAndOrder(market.getId(), order)) {
			throw new BusinessException(ErrorCode.DUPLICATED_OFFER);
		}

		if (order.isPassedVisitDate(LocalDateTime.now())) {
			throw new BusinessException(ErrorCode.VISIT_DATE_PASSED);
		}

		if (order.isClosed()) {
			throw new BusinessException(ErrorCode.ORDER_CLOSED);
		}
	}

	private Market getMarket(Member member) {
		return marketRepository.findByMember(member)
				.orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));
	}

	private Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

	private Order getOrder(Long orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

}