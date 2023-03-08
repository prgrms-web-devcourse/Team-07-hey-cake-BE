package com.programmers.heycake.domain.offer.controller;

import static com.programmers.heycake.util.TestUtils.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
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
	ObjectMapper objectMapper;

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

	void setContext(Member member) {
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(
				new UsernamePasswordAuthenticationToken(member.getId(), null,
						List.of(new SimpleGrantedAuthority("ROLE_USER"))));
	}

	@Nested
	@DisplayName("deleteOffer")
	@Transactional
	class DeleteOffer {
		@Test
		@DisplayName("Success - Offer 를 삭제한다.")
		void createHistorySuccess() throws Exception {
			//given
			Member member = memberRepository.save(new Member("rhdtn311@naver.com", MemberAuthority.USER, "0000"));
			Member member1 = memberRepository.save(new Member("rhdtn3211@naver.com", MemberAuthority.USER, "0000"));

			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(
					new UsernamePasswordAuthenticationToken(member.getId(), null,
							List.of(new SimpleGrantedAuthority("ROLE_MARKET"))));

			Order order = orderRepository.save(getOrder(member1.getId()));

			MarketEnrollment marketEnrollment = getMarketEnrollment();
			marketEnrollment.setMember(member);
			marketEnrollmentRepository.save(marketEnrollment);

			Market market = getMarket();
			market.setMember(member);
			market.setMarketEnrollment(marketEnrollment);
			marketRepository.save(market);
			member.changeAuthority(MemberAuthority.MARKET);

			Offer offer = getOffer(market.getId(), 1000, "content");
			offer.setOrder(order);
			offerRepository.save(offer);

			//when //then
			mockMvc.perform(delete("/api/v1/offers/{offerId}", offer.getId())
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isNoContent())
					.andDo(print())
					.andDo(document(
							"offer/제안 삭제",
							requestHeaders(
									headerWithName("access_token").description("access token")
							),
							pathParameters(
									parameterWithName("offerId").description("offer id")
							)
					));
		}

		@Test
		@DisplayName("Fail - Offer 삭제 실패.(BadRequest)")
		void createHistoryBadRequest() throws Exception {
			//given
			Member member = memberRepository.save(new Member("rhdtn311@naver.com", MemberAuthority.USER, "0000"));

			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(
					new UsernamePasswordAuthenticationToken(member.getId(), null,
							List.of(new SimpleGrantedAuthority("ROLE_MARKET"))));

			//when //then
			mockMvc.perform(delete("/api/v1/offers/{offerId}", -1)
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document(
							"offer/제안 삭제 실패(BadRequest)",
							requestHeaders(
									headerWithName("access_token").description("access token")
							),
							pathParameters(
									parameterWithName("offerId").description("offer id")
							)
					));
		}

		@Test
		@DisplayName("Fail - Offer 삭제 실패.(Forbidden)")
		void createHistoryForbidden() throws Exception {
			//given
			Member member = memberRepository.save(new Member("rhdtn311@naver.com", MemberAuthority.USER, "0000"));
			Member member1 = memberRepository.save(new Member("rhdtn3211@naver.com", MemberAuthority.USER, "0000"));
			Member anotherMember = memberRepository.save(new Member("rhdtn321@naver.com", MemberAuthority.USER, "0000"));

			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(
					new UsernamePasswordAuthenticationToken(member.getId(), null,
							List.of(new SimpleGrantedAuthority("ROLE_MARKET"))));

			Order order = orderRepository.save(getOrder(member1.getId()));

			MarketEnrollment marketEnrollment = getMarketEnrollment();
			marketEnrollment.setMember(member);
			marketEnrollmentRepository.save(marketEnrollment);

			Market market = getMarket();
			market.setMember(member);
			market.setMarketEnrollment(marketEnrollment);
			marketRepository.save(market);
			member.changeAuthority(MemberAuthority.MARKET);

			Market anotherMarket = getMarket();
			anotherMarket.setMember(anotherMember);
			anotherMarket.setMarketEnrollment(marketEnrollment);
			marketRepository.save(anotherMarket);
			anotherMember.changeAuthority(MemberAuthority.MARKET);

			Offer offer = getOffer(anotherMarket.getId(), 1000, "content");
			offer.setOrder(order);
			offerRepository.save(offer);

			//when //then
			mockMvc.perform(delete("/api/v1/offers/{offerId}", offer.getId())
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document(
							"offer/제안 삭제 실패(Forbidden)",
							requestHeaders(
									headerWithName("access_token").description("access token")
							),
							pathParameters(
									parameterWithName("offerId").description("offer id")
							)
					));
		}

		@Test
		@DisplayName("Fail - Offer 삭제 실패.(Conflict)")
		void createHistoryConflict() throws Exception {
			//given
			Member member = memberRepository.save(new Member("rhdtn311@naver.com", MemberAuthority.USER, "0000"));
			Member member1 = memberRepository.save(new Member("rhdtn3211@naver.com", MemberAuthority.USER, "0000"));

			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(
					new UsernamePasswordAuthenticationToken(member.getId(), null,
							List.of(new SimpleGrantedAuthority("ROLE_MARKET"))));

			Order order = orderRepository.save(getOrder(member1.getId()));

			MarketEnrollment marketEnrollment = getMarketEnrollment();
			marketEnrollment.setMember(member);
			marketEnrollmentRepository.save(marketEnrollment);

			Market market = getMarket();
			market.setMember(member);
			market.setMarketEnrollment(marketEnrollment);
			marketRepository.save(market);
			member.changeAuthority(MemberAuthority.MARKET);

			Offer offer = getOffer(market.getId(), 1000, "content");
			offer.setOrder(order);
			offerRepository.save(offer);

			order.upDateOrderStatus(OrderStatus.RESERVED);

			//when //then
			mockMvc.perform(delete("/api/v1/offers/{offerId}", offer.getId())
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isConflict())
					.andDo(print())
					.andDo(document(
							"offer/제안 삭제",
							requestHeaders(
									headerWithName("access_token").description("access token")
							),
							pathParameters(
									parameterWithName("offerId").description("offer id")
							)
					));
		}

	}

}
