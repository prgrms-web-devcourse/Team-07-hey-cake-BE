package com.programmers.heycake.domain.order.controller;

import static com.programmers.heycake.util.TestUtils.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.facade.HistoryFacade;
import com.programmers.heycake.domain.order.model.dto.request.HistoryControllerRequest;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.repository.OrderRepository;
import com.programmers.heycake.util.WithMockCustomUser;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class HistoryControllerTest {
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

	@BeforeEach
	void createOffer() {
		//offer 생성
	}

	@Nested
	@DisplayName("createHistory")
	@Transactional
	class CreateHistory {
		@Test
		@WithMockCustomUser
		@DisplayName("Success - orderHistory 를 생성한다.")
		void createHistorySuccess() throws Exception {
			//given
			// Member member = memberRepository.findById(2L).get();
			Order order = orderRepository.save(getOrder(2L));
			Market market = marketRepository.save(getMarket());
			Offer offer = getOffer(market.getId(), 1000, "content");
			offer.setOrder(order);
			offerRepository.save(offer);

			HistoryControllerRequest historyControllerRequest =
					new HistoryControllerRequest(order.getId(), offer.getId());

			//when //then
			mockMvc.perform(post("/api/v1/histories")
							.header("access_token",
									"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaXNzIjoiaGV5LWNha2UiLCJleHAiOjM2NzgwOTQyNTMsImlhdCI6MTY3ODA5NDI1MywibWVtYmVySWQiOjJ9.efMIPCAP9jf6-HklFpQ832Ur50LSLq-H6_7Tcwemh7wPc7NrVJIherhvdoxIXA7NWl9xm1mQsKgzbnRD6MuB1g")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(historyControllerRequest))
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
								"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaXNzIjoiaGV5LWNha2UiLCJleHAiOjM2NzgwNTE5MzEsImlhdCI6MTY3ODA1MTkzMSwibWVtYmVySWQiOjJ9.Kou7wVDRO6v9Bb-bBOczLcNWzePtih9RTTVO6349qCqDEzgIezfwlVWj7se3u4T1sqPGL_Xr1dQRt7p0uXUJ9A")
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
