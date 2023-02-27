package com.programmers.heycake.domain.offer.service;

import static com.programmers.heycake.utils.TestUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

	private static final String ERROR_CODE = "errorCode";

	@InjectMocks
	private OfferService offerService;

	@Mock
	private OfferRepository offerRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private MarketRepository marketRepository;

	@Mock
	private Market market;

	@Mock
	private Order order;

	@Nested
	@Transactional
	class saveOffer {

		@Test
		@DisplayName("Success - 주문 생성 성공 - saveOffer")
		void saveOfferSuccess() {
			// given
			Member member = getMember();

			when(orderRepository.findById(any(Long.class)))
					.thenReturn(Optional.of(order));
			when(memberRepository.findById(any(Long.class)))
					.thenReturn(Optional.of(member));
			when(marketRepository.findByMember(any(Member.class)))
					.thenReturn(Optional.of(market));
			when(offerRepository.existsByMarketIdAndOrder(any(Long.class), any(Order.class)))
					.thenReturn(false);
			when(market.getId())
					.thenReturn(1L);

			// when
			offerService.saveOffer(1L, 1L, 10, "content");

			// then
			verify(orderRepository).findById(any(Long.class));
			verify(memberRepository).findById(any(Long.class));
			verify(marketRepository).findByMember(any(Member.class));
			verify(offerRepository).existsByMarketIdAndOrder(any(Long.class), any(Order.class));
			verify(market, times(2)).getId();
		}

		@Test
		@DisplayName("Fail - 존재하지 않는 주문인 경우 실패한다. - saveOffer")
		void saveOfferNotExistsOrderFail() throws Exception {
			// given
			when(orderRepository.findById(any(Long.class)))
					.thenReturn(Optional.empty());

			// when, then
			assertThatThrownBy(() -> offerService.saveOffer(1L, 1L, 10, "content"))
					.isExactlyInstanceOf(BusinessException.class)
					.hasFieldOrPropertyWithValue(ERROR_CODE, ErrorCode.ENTITY_NOT_FOUND);

			verify(orderRepository).findById(any(Long.class));
		}

		@Test
		@DisplayName("Fail - 글 작성 회원이 업주가 아닌 경우 실패한다. - saveOffer")
		void saveOfferWriterIsNotMarketFail() throws Exception {
			// given
			Member member = getMember();

			when(orderRepository.findById(any(Long.class)))
					.thenReturn(Optional.of(order));
			when(memberRepository.findById(any(Long.class)))
					.thenReturn(Optional.of(member));
			when(marketRepository.findByMember(any(Member.class)))
					.thenReturn(Optional.empty());

			// when, then
			assertThatThrownBy(() -> offerService.saveOffer(1L, 1L, 10, "content"))
					.isExactlyInstanceOf(BusinessException.class)
					.hasFieldOrPropertyWithValue(ERROR_CODE, ErrorCode.FORBIDDEN);

			verify(orderRepository).findById(any(Long.class));
			verify(memberRepository).findById(any(Long.class));
			verify(marketRepository).findByMember(any(Member.class));
		}

		@Test
		@DisplayName("Fail - 이미 제안 글을 작성하적 있는 업주인 경우 실패한다. - saveOffer")
		void saveOfferAlreadyWriteOfferFail() throws Exception {
			// given
			Member member = getMember();

			when(orderRepository.findById(any(Long.class)))
					.thenReturn(Optional.of(order));
			when(memberRepository.findById(any(Long.class)))
					.thenReturn(Optional.of(member));
			when(marketRepository.findByMember(any(Member.class)))
					.thenReturn(Optional.of(market));
			when(offerRepository.existsByMarketIdAndOrder(any(Long.class), any(Order.class)))
					.thenReturn(true);

			// when, then
			assertThatThrownBy(() -> offerService.saveOffer(1L, 1L, 10, "content"))
					.isExactlyInstanceOf(BusinessException.class)
					.hasFieldOrPropertyWithValue(ERROR_CODE, ErrorCode.DUPLICATED_OFFER);

			verify(orderRepository).findById(any(Long.class));
			verify(memberRepository).findById(any(Long.class));
			verify(marketRepository).findByMember(any(Member.class));
			verify(offerRepository).existsByMarketIdAndOrder(any(Long.class), any(Order.class));
		}

		@Test
		@DisplayName("Fail - 픽업 날짜가 지난 주문인 경우 실패한다. - saveOffer")
		void saveOfferPassedByVisitDateFail() throws Exception {
			// given
			Member member = getMember();

			when(orderRepository.findById(any(Long.class)))
					.thenReturn(Optional.of(order));
			when(memberRepository.findById(any(Long.class)))
					.thenReturn(Optional.of(member));
			when(marketRepository.findByMember(any(Member.class)))
					.thenReturn(Optional.of(market));
			when(offerRepository.existsByMarketIdAndOrder(any(Long.class), any(Order.class)))
					.thenReturn(true);

			// when, then
			assertThatThrownBy(() -> offerService.saveOffer(1L, 1L, 10, "content"))
					.isExactlyInstanceOf(BusinessException.class)
					.hasFieldOrPropertyWithValue(ERROR_CODE, ErrorCode.DUPLICATED_OFFER);

			verify(orderRepository).findById(any(Long.class));
			verify(memberRepository).findById(any(Long.class));
			verify(marketRepository).findByMember(any(Member.class));
			verify(offerRepository).existsByMarketIdAndOrder(any(Long.class), any(Order.class));
		}

		@Test
		@DisplayName("Fail - 이미 완료된 주문인 경우 실패한다. - saveOffer")
		void saveOfferAlreadyDoneOrderFail() throws Exception {
			// given
			Member member = getMember();

			when(orderRepository.findById(any(Long.class)))
					.thenReturn(Optional.of(order));
			when(memberRepository.findById(any(Long.class)))
					.thenReturn(Optional.of(member));
			when(marketRepository.findByMember(any(Member.class)))
					.thenReturn(Optional.of(market));
			when(offerRepository.existsByMarketIdAndOrder(any(Long.class), any(Order.class)))
					.thenReturn(false);
			when(order.isClosed())
					.thenReturn(true);

			// when, then
			assertThatThrownBy(() -> offerService.saveOffer(1L, 1L, 10, "content"))
					.isExactlyInstanceOf(BusinessException.class)
					.hasFieldOrPropertyWithValue(ERROR_CODE, ErrorCode.ORDER_CLOSED);

			verify(orderRepository).findById(any(Long.class));
			verify(memberRepository).findById(any(Long.class));
			verify(marketRepository).findByMember(any(Member.class));
			verify(offerRepository).existsByMarketIdAndOrder(any(Long.class), any(Order.class));
			verify(market).getId();
		}
	}
}