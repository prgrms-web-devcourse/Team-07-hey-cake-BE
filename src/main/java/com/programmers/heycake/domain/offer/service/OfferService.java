package com.programmers.heycake.domain.offer.service;

import static com.programmers.heycake.common.mapper.OfferMapper.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.common.mapper.OfferMapper;
import com.programmers.heycake.common.utils.AuthenticationUtil;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.offer.model.dto.OfferDto;
import com.programmers.heycake.domain.offer.model.dto.response.OfferResponse;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.programmers.heycake.domain.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfferService {
	private final OfferRepository offerRepository;
	private final MemberRepository memberRepository;
	private final OrderRepository orderRepository;
	private final MarketRepository marketRepository;

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteOffer(Long offerId, Long marketId) {
		if (!Objects.equals(getOfferById(offerId).marketId(), marketId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
		if (isReservedOffer(offerId)) {
			throw new BusinessException(ErrorCode.DELETE_ERROR);
		}

		deleteOfferWithoutAuth(offerId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteOfferWithoutAuth(Long offerId) {
		offerRepository.deleteById(offerId);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public OfferDto getOfferById(Long offerId) {
		return toOfferDto(getOffer(offerId));
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean isReservedOffer(Long offerId) {
		return !getOfferById(offerId)
				.orderDto()
				.orderStatus()
				.equals(OrderStatus.NEW);
	}

	public Long saveOffer(Long orderId, int expectedPrice, String content) {
		Long memberId = AuthenticationUtil.getMemberId();

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

	public List<OfferResponse> getOffersWithComments(Long orderId) {
		Order order = getOrder(orderId);

		return offerRepository.findAllByOrderFetchComments(order)
				.stream()
				.map(OfferMapper::toOfferResponse)
				.toList();
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

	private Offer getOffer(Long offerId) {
		return offerRepository
				.findByIdWithFetchJoin(offerId)
				.orElseThrow(
						() -> {
							throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage());
						});
	}
}
