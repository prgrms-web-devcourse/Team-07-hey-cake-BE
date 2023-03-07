package com.programmers.heycake.domain.order.controller;

import static com.programmers.heycake.util.TestUtils.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
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
import com.programmers.heycake.domain.member.service.MemberService;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.facade.HistoryFacade;
import com.programmers.heycake.domain.order.model.dto.request.HistoryControllerRequest;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.repository.OrderRepository;
import com.programmers.heycake.util.WithMockCustomUserSecurityContextFactory;

@Transactional
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class HistoryControllerTest {

	private static final String ACCESS_TOKEN = "access_token";

	@Autowired
	MockMvc mockMvc;

	@Autowired
	HistoryFacade historyFacade;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	MarketRepository marketRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OfferRepository offerRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	MarketEnrollmentRepository marketEnrollmentRepository;

	@Autowired
	MemberService memberService;

	@Autowired
	WithMockCustomUserSecurityContextFactory withMockCustomUserSecurityContextFactory;

	@Nested
	@DisplayName("createHistory")
	@Transactional
	class CreateHistory {
		@Test
		@DisplayName("Success - orderHistory 를 생성한다.")
		void createHistorySuccess() throws Exception {
			//given
			Member member = memberRepository.save(new Member("rhdtn311@naver.com", MemberAuthority.USER, "0000"));

			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(
					new UsernamePasswordAuthenticationToken(member.getId(), null,
							List.of(new SimpleGrantedAuthority("ROLE_USER"))));

			Order order = orderRepository.save(getOrder(member.getId()));

			MarketEnrollment marketEnrollment = getMarketEnrollment();
			marketEnrollment.setMember(member);
			marketEnrollmentRepository.save(marketEnrollment);

			Market market = getMarket();
			market.setMember(member);
			market.setMarketEnrollment(marketEnrollment);
			marketRepository.save(market);

			Offer offer = getOffer(market.getId(), 1000, "content");
			offer.setOrder(order);
			offerRepository.save(offer);

			HistoryControllerRequest historyControllerRequest =
					new HistoryControllerRequest(order.getId(), offer.getId());

			//when //then
			mockMvc.perform(post("/api/v1/histories")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(historyControllerRequest))
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isCreated())
					.andDo(print())
					.andDo(document(
							"history/주문 확정 생성",
							requestHeaders(
									headerWithName("access_token").description("access token")
							),
							requestFields(
									fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("orderId"),
									fieldWithPath("offerId").type(JsonFieldType.NUMBER).description("offerId")
							),
							responseHeaders(
									headerWithName("location").description("saved location")
							)
					));
		}
	}

	@Test
	@DisplayName("Fail - 회원 인증 실패")
	void createHistoryUnauthorizedFail() throws Exception {
		//given
		HistoryControllerRequest historyControllerRequest = new HistoryControllerRequest(2L, 2L);

		//when //then
		mockMvc.perform(post("/api/v1/histories")
						.header("access_token", "asdf")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(historyControllerRequest))
						.with(csrf())
				).andExpect(status().isUnauthorized())
				.andDo(print())
				.andDo(document(
						"history/사용자 인증 오류",
						requestHeaders(
								headerWithName("access_token").description("access token")
						),
						requestFields(
								fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("orderId"),
								fieldWithPath("offerId").type(JsonFieldType.NUMBER).description("offerId")
						)
				));
	}

	@Test
	@DisplayName("Fail - 회원 권한 없음.")
	void createHistoryForbiddenFail() throws Exception {
		//given
		setContextHolder(2L, "USER");
		HistoryControllerRequest historyControllerRequest = new HistoryControllerRequest(2L, 2L);

		//when //then
		mockMvc.perform(post("/api/v1/histories")
						.header("access_token",
								"TOKEN")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(historyControllerRequest))
						.with(csrf())
				).andExpect(status().isForbidden())
				.andDo(print())
				.andDo(document(
						"history/사용자 권한 오류",
						requestHeaders(
								headerWithName("access_token").description("access token")
						),
						requestFields(
								fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("orderId"),
								fieldWithPath("offerId").type(JsonFieldType.NUMBER).description("offerId")
						)
				));
	}

}
