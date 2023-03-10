package com.programmers.heycake.domain.offer.controller;

import static com.programmers.heycake.util.TestUtils.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.model.vo.MemberAuthority;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.programmers.heycake.domain.order.repository.OrderRepository;

@Transactional
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class OfferControllerTest {
	private static final String ACCESS_TOKEN = "access_token";
	@Autowired
	MockMvc mockMvc;

	@Autowired
	OfferRepository offerRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	MarketEnrollmentRepository marketEnrollmentRepository;

	@Autowired
	MarketRepository marketRepository;

	private Offer setTestOffer(Order order, Market market) {
		Offer offer = getOffer(market.getId(), 1000, "content");
		offer.setOrder(order);
		offerRepository.save(offer);
		return offer;
	}

	private Market setTestMarket(Member marketMember, MarketEnrollment marketEnrollment) {
		Market market = getMarket();
		market.setMember(marketMember);
		market.setMarketEnrollment(marketEnrollment);
		marketRepository.save(market);
		marketMember.changeAuthority(MemberAuthority.MARKET);
		return market;
	}

	private MarketEnrollment setTestMarketEnrollment(Member marketMember) {
		MarketEnrollment marketEnrollment = getMarketEnrollment("1231231231");
		marketEnrollment.setMember(marketMember);
		marketEnrollmentRepository.save(marketEnrollment);
		return marketEnrollment;
	}

	@Nested
	@DisplayName("deleteOffer")
	@Transactional
	class DeleteOffer {
		@Test
		@DisplayName("Success - Offer 를 삭제한다.")
		void deleteOfferSuccess() throws Exception {
			//given
			Member marketMember = memberRepository.save(getMember("marketMember"));
			Member member = memberRepository.save(getMember("member"));
			setContext(marketMember.getId(), MemberAuthority.MARKET);

			Order order = orderRepository.save(getOrder(member.getId()));

			MarketEnrollment marketEnrollment = setTestMarketEnrollment(marketMember);
			Market market = setTestMarket(marketMember, marketEnrollment);

			Offer offer = setTestOffer(order, market);

			//when //then
			mockMvc.perform(delete("/api/v1/offers/{offerId}", offer.getId())
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isNoContent())
					.andDo(print())
					.andDo(document(
							"offers/제안 삭제 성공",
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("offerId").description("제안 식별자")
							)
					));
		}

		@Test
		@DisplayName("Fail - Offer 삭제 실패.(BadRequest)")
		void deleteOfferBadRequest() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), MemberAuthority.MARKET);

			//when //then
			mockMvc.perform(delete("/api/v1/offers/{offerId}", -1)
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document(
							"offers/제안 삭제 실패(BadRequest)",
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("offerId").description("제안 식별자")
							)
					));
		}

		@Test
		@DisplayName("Fail - Offer 삭제 실패.(Unauthorized)")
		void deleteOfferUnauthorized() throws Exception {
			//given

			//when //then
			mockMvc.perform(delete("/api/v1/offers/{offerId}", 1)
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"offers/제안 삭제 실패(Unauthorized)",
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("offerId").description("제안 식별자")
							)
					));
		}

		@Test
		@DisplayName("Fail - Offer 삭제 실패.(Forbidden)")
		void deleteOfferForbidden() throws Exception {
			//given
			Member marketMember = memberRepository.save(getMember("marketMember"));
			Member anotherMarketMember = memberRepository.save(getMember("anotherMarketMember"));
			Member member = memberRepository.save(getMember("member"));
			setContext(marketMember.getId(), MemberAuthority.MARKET);

			Order order = orderRepository.save(getOrder(member.getId()));

			MarketEnrollment marketEnrollment = setTestMarketEnrollment(marketMember);
			setTestMarket(marketMember, marketEnrollment);
			Market anotherMarket = setTestMarket(anotherMarketMember, marketEnrollment);

			Offer offer = setTestOffer(order, anotherMarket);

			//when //then
			mockMvc.perform(delete("/api/v1/offers/{offerId}", offer.getId())
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document(
							"offers/제안 삭제 실패(Forbidden)",
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("offerId").description("제안 식별자")
							)
					));
		}

		@Test
		@DisplayName("Fail - Offer 삭제 실패.(Conflict)")
		void deleteOfferConflict() throws Exception {
			//given
			Member marketMember = memberRepository.save(getMember("marketMember"));
			Member member = memberRepository.save(getMember("member"));
			setContext(marketMember.getId(), MemberAuthority.MARKET);

			Order order = orderRepository.save(getOrder(member.getId()));
			order.upDateOrderStatus(OrderStatus.RESERVED);

			MarketEnrollment marketEnrollment = setTestMarketEnrollment(marketMember);
			Market market = setTestMarket(marketMember, marketEnrollment);

			Offer offer = setTestOffer(order, market);

			//when //then
			mockMvc.perform(delete("/api/v1/offers/{offerId}", offer.getId())
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isConflict())
					.andDo(print())
					.andDo(document(
							"offers/제안 삭제 실패(Conflict)",
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("offerId").description("제안 식별자")
							)
					));
		}

	}

}
