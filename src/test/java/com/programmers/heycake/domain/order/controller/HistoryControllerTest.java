package com.programmers.heycake.domain.order.controller;

import static com.programmers.heycake.common.util.ApiDocumentUtils.*;
import static com.programmers.heycake.common.util.TestUtils.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.model.vo.MemberAuthority;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.model.dto.request.HistoryCreateControllerRequest;
import com.programmers.heycake.domain.order.model.dto.request.UpdateSugarScoreRequest;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;
import com.programmers.heycake.domain.order.repository.HistoryRepository;
import com.programmers.heycake.domain.order.repository.OrderRepository;

@Transactional
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class HistoryControllerTest {

	private static final String ACCESS_TOKEN = "access_token";
	private static final String INVALID_ACCESS_TOKEN = "access_token";

	@Autowired
	MockMvc mockMvc;

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
	HistoryRepository historyRepository;

	@Autowired
	MarketEnrollmentRepository marketEnrollmentRepository;

	@Nested
	@DisplayName("createHistory")
	@Transactional
	class CreateHistory {
		@Test
		@DisplayName("Success - orderHistory 를 생성한다.")
		void createHistorySuccess() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), MemberAuthority.USER);

			Order order = orderRepository.save(getOrder(member.getId()));

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231234", member));

			Market market = marketRepository.save(getMarket(member, marketEnrollment));

			Offer offer = offerRepository.save(getOffer(order, market));

			HistoryCreateControllerRequest historyControllerRequest =
					new HistoryCreateControllerRequest(order.getId(), offer.getId(), true);

			//when //then
			mockMvc.perform(post("/api/v1/histories")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(historyControllerRequest))
							.header("access_token", ACCESS_TOKEN)
							.with(csrf()))
					.andExpect(status().isCreated())
					.andDo(print())
					.andDo(
							document("history/결제 내역 생성 성공",
									getDocumentRequest(),
									getDocumentResponse(),
									requestHeaders(
											headerWithName("access_token").description("인가 토큰")
									),
									requestFields(
											fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("주문 식별자"),
											fieldWithPath("offerId").type(JsonFieldType.NUMBER).description("제안 식별자"),
											fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("결제 여부")
									),
									responseHeaders(
											headerWithName("location").description("저장 경로")
									)
							));
		}

		@Test
		@DisplayName("Fail - orderHistory 생성 실패.(BadRequest)")
		void createHistoryBadRequest() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			Member anotherMember = memberRepository.save(getMember("anotherMember"));
			setContext(member.getId(), MemberAuthority.USER);

			Order order = orderRepository.save(getOrder(member.getId()));

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1234123412", anotherMember));

			Market market = marketRepository.save(getMarket(member, marketEnrollment));

			offerRepository.save(getOffer(order, market));
			Order anotherOrder = orderRepository.save(getOrder(anotherMember.getId()));

			MarketEnrollment anotherMarketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231234", anotherMember));

			Market anotherMarket = marketRepository.save(getMarket(anotherMember, anotherMarketEnrollment));

			Offer anotherOffer = offerRepository.save(getOffer(anotherOrder, anotherMarket));

			HistoryCreateControllerRequest historyControllerRequest =
					new HistoryCreateControllerRequest(order.getId(), anotherOffer.getId(), true);

			//when //then
			mockMvc.perform(post("/api/v1/histories")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(historyControllerRequest))
							.header("access_token", ACCESS_TOKEN)
							.with(csrf()))
					.andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(
							document("history/결제 내역 생성 실패(BadRequest)",
									getDocumentRequest(),
									getDocumentResponse(),
									requestHeaders(
											headerWithName("access_token").description("인가 토큰")
									),
									requestFields(
											fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("주문 식별자"),
											fieldWithPath("offerId").type(JsonFieldType.NUMBER).description("제안 식별자"),
											fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("결제 여부")
									),
									responseFields(
											fieldWithPath("message").description("에러 메세지"),
											fieldWithPath("path").description("에러 발생 uri"),
											fieldWithPath("time").description("에러 발생 시각"),
											fieldWithPath("inputErrors").description("에러 상세")
									)
							));
		}

		@Test
		@DisplayName("Fail - orderHistory 생성실패.(Unauthorized)")
		void createHistoryUnauthorized() throws Exception {
			//given
			HistoryCreateControllerRequest historyControllerRequest = new HistoryCreateControllerRequest(2L, 2L, true);

			//when //then
			mockMvc.perform(post("/api/v1/histories")
							.header("access_token", INVALID_ACCESS_TOKEN)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(historyControllerRequest))
							.with(csrf()))
					.andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(
							document("history/결제 내역 생성 실패(Unauthorized)",
									getDocumentRequest(),
									getDocumentResponse(),
									requestHeaders(
											headerWithName("access_token").description("인가 토큰")
									),
									requestFields(
											fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("주문 식별자"),
											fieldWithPath("offerId").type(JsonFieldType.NUMBER).description("제안 식별자"),
											fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("결제 여부")
									)
							));
		}

		@Test
		@DisplayName("Fail - orderHistory 생성실패.(Forbidden)")
		void createHistoryForbidden() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			Member anotherMember = memberRepository.save(getMember("anotherMember"));
			setContext(member.getId(), MemberAuthority.USER);

			Order order = orderRepository.save(getOrder(member.getId()));

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1234123412", anotherMember));

			Market market = marketRepository.save(getMarket(member, marketEnrollment));

			offerRepository.save(getOffer(order, market));
			Order anotherOrder = orderRepository.save(getOrder(anotherMember.getId()));

			MarketEnrollment anotherMarketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231231", anotherMember));

			Market anotherMarket = marketRepository.save(getMarket(anotherMember, anotherMarketEnrollment));

			Offer anotherOffer = offerRepository.save(getOffer(anotherOrder, anotherMarket));

			HistoryCreateControllerRequest historyControllerRequest =
					new HistoryCreateControllerRequest(anotherOrder.getId(), anotherOffer.getId(), true);

			//when //then
			mockMvc.perform(post("/api/v1/histories")
							.header("access_token", ACCESS_TOKEN)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(historyControllerRequest))
							.with(csrf()))
					.andExpect(status().isForbidden())
					.andDo(print())
					.andDo(
							document("history/결제 내역 생성 실패(Forbidden)",
									getDocumentRequest(),
									getDocumentResponse(),
									requestHeaders(
											headerWithName("access_token").description("인가 토큰")
									),
									requestFields(
											fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("주문 식별자"),
											fieldWithPath("offerId").type(JsonFieldType.NUMBER).description("제안 식별자"),
											fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("결제 여부")
									)
							));
		}

		@Test
		@DisplayName("Fail - orderHistory 생성실패.(NotFound)")
		void createHistoryNotFound() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), MemberAuthority.USER);

			HistoryCreateControllerRequest historyControllerRequest =
					new HistoryCreateControllerRequest(0L, 0L, true);

			//when //then
			mockMvc.perform(post("/api/v1/histories")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(historyControllerRequest))
							.header("access_token", ACCESS_TOKEN)
							.with(csrf()))
					.andExpect(status().isNotFound())
					.andDo(print())
					.andDo(
							document("history/결제 내역 생성 실패(NotFound)",
									getDocumentRequest(),
									getDocumentResponse(),
									requestHeaders(
											headerWithName("access_token").description("인가 토큰")
									),
									requestFields(
											fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("주문 식별자"),
											fieldWithPath("offerId").type(JsonFieldType.NUMBER).description("제안 식별자"),
											fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("결제 여부")
									),
									responseFields(
											fieldWithPath("message").description("에러 메세지"),
											fieldWithPath("path").description("에러 발생 uri"),
											fieldWithPath("time").description("에러 발생 시각"),
											fieldWithPath("inputErrors").description("에러 상세")
									)
							));
		}

	}

	@Nested
	@DisplayName("updateSugarScore")
	@Transactional
	class UpdateSugarScore {

		@Test
		@DisplayName("Success - 당도 갱신에 성공하여 204 응답으로 한다.")
		void updateSugarScoreSuccess() throws Exception {
			Member member = memberRepository.save(getMember("member@gmail.com"));
			setContext(member.getId(), MemberAuthority.USER);

			Order order = orderRepository.save(getOrder(member.getId()));

			MarketEnrollment marketEnrollment = setTestMarketEnrollment(member, "1231231234");
			Market market = setTestMarket(member, marketEnrollment);

			Offer offer = setTestOffer(order, market);
			offer.setOrder(order);

			OrderHistory orderHistory = getOrderHistory(member.getId(), market.getId(), order);
			historyRepository.save(orderHistory);

			UpdateSugarScoreRequest updateSugarScoreRequest = new UpdateSugarScoreRequest(orderHistory.getId(), 30);

			//when //then
			mockMvc.perform(post("/api/v1/histories/sugar-score")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(updateSugarScoreRequest))
							.header("access_token", ACCESS_TOKEN)
							.with(csrf()))
					.andExpect(status().isNoContent())
					.andDo(print())
					.andDo(
							document("order/내 주문 당도 갱신 성공",
									getDocumentRequest(),
									getDocumentResponse(),
									requestHeaders(
											headerWithName("access_token").description("인가 토큰")
									),
									requestFields(
											fieldWithPath("orderHistoryId").type(JsonFieldType.NUMBER).description("주문 히스토리 식별자"),
											fieldWithPath("sugarScore").type(JsonFieldType.NUMBER).description("주문에 대한 만족도 점수")
									)));
		}

		@ParameterizedTest
		@ValueSource(ints = {-1, -4, 101, 140, 200})
		@DisplayName("Fail - 당도 갱신값이 잘못되어 400으로 응답으로 한다.")
		void updateSugarScoreFailByBadRequest(int score) throws Exception {
			Member member = memberRepository.save(getMember("member@gmail.com"));
			setContext(member.getId(), MemberAuthority.USER);

			Order order = orderRepository.save(getOrder(member.getId()));

			MarketEnrollment marketEnrollment = setTestMarketEnrollment(member, "1231231234");
			Market market = setTestMarket(member, marketEnrollment);

			Offer offer = setTestOffer(order, market);
			offer.setOrder(order);

			OrderHistory orderHistory = getOrderHistory(member.getId(), market.getId(), order);
			historyRepository.save(orderHistory);

			UpdateSugarScoreRequest updateSugarScoreRequest = new UpdateSugarScoreRequest(orderHistory.getId(), score);

			//when //then
			mockMvc.perform(post("/api/v1/histories/sugar-score")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(updateSugarScoreRequest))
							.header("access_token", ACCESS_TOKEN)
							.with(csrf()))
					.andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(
							document("order/내 주문 당도 갱신 실패 - 당도값 범위 벗어난 경우",
									getDocumentRequest(),
									getDocumentResponse(),
									requestHeaders(
											headerWithName("access_token").description("인가 토큰")
									),
									requestFields(
											fieldWithPath("orderHistoryId").type(JsonFieldType.NUMBER).description("주문 히스토리 식별자"),
											fieldWithPath("sugarScore").type(JsonFieldType.NUMBER).description("주문에 대한 만족도 점수")
									),
									responseFields(
											fieldWithPath("message").type(JsonFieldType.STRING).description("오류 메시지"),
											fieldWithPath("path").type(JsonFieldType.STRING).description("오류 경로"),
											fieldWithPath("time").type(JsonFieldType.STRING).description("오류 발생한 시간"),
											fieldWithPath("inputErrors").type(JsonFieldType.ARRAY).description("오류가 발생한 필드 리스트"),
											fieldWithPath("inputErrors[].field").type(JsonFieldType.STRING).description("오류가 발생한 필드"),
											fieldWithPath("inputErrors[].rejectedValue").type(JsonFieldType.NUMBER).description("오류가 발생한 값"),
											fieldWithPath("inputErrors[].message").type(JsonFieldType.STRING).description("필드에 대한 오류 메시지")
									)));
		}

		@Test
		@DisplayName("Fail - 인증이 되지 않아 401으로 응답으로 한다.")
		void updateSugarScoreFailByUnAuthorized() throws Exception {
			Member member = memberRepository.save(getMember("member@gmail.com"));
			Order order = orderRepository.save(getOrder(member.getId()));

			MarketEnrollment marketEnrollment = setTestMarketEnrollment(member, "1231231234");
			Market market = setTestMarket(member, marketEnrollment);

			Offer offer = setTestOffer(order, market);
			offer.setOrder(order);

			OrderHistory orderHistory = getOrderHistory(member.getId(), market.getId(), order);
			historyRepository.save(orderHistory);

			UpdateSugarScoreRequest updateSugarScoreRequest = new UpdateSugarScoreRequest(orderHistory.getId(), 101);

			//when //then
			mockMvc.perform(post("/api/v1/histories/sugar-score")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(updateSugarScoreRequest))
							.header("access_token", INVALID_ACCESS_TOKEN)
							.with(csrf()))
					.andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(
							document("order/내 주문 당도 수정 실패 - 인증이 되지 않은 경우",
									getDocumentRequest(),
									getDocumentResponse(),
									requestHeaders(
											headerWithName("access_token").description("인가 토큰")
									),
									requestFields(
											fieldWithPath("orderHistoryId").type(JsonFieldType.NUMBER).description("주문 히스토리 식별자"),
											fieldWithPath("sugarScore").type(JsonFieldType.NUMBER).description("주문에 대한 만족도 점수")
									),
									responseFields(
											fieldWithPath("message").type(JsonFieldType.STRING).description("오류 메시지"),
											fieldWithPath("path").type(JsonFieldType.STRING).description("오류 경로"),
											fieldWithPath("time").type(JsonFieldType.STRING).description("오류 발생한 시간"),
											fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("오류가 발생한 필드")
									)));
		}
	}

	@Test
	@DisplayName("Fail - 주문 작성자와 평점 작성자가 달라 403으로 응답으로 한다.")
	void updateSugarScoreFailByBadRequest() throws Exception {
		Member member = memberRepository.save(getMember("member@gmail.com"));
		setContext(member.getId(), MemberAuthority.USER);

		Order order = orderRepository.save(getOrder(member.getId()));

		MarketEnrollment marketEnrollment = setTestMarketEnrollment(member, "1231231234");
		Market market = setTestMarket(member, marketEnrollment);

		Offer offer = setTestOffer(order, market);
		offer.setOrder(order);

		OrderHistory orderHistory = getOrderHistory(member.getId() + 1L, market.getId(), order);
		historyRepository.save(orderHistory);

		UpdateSugarScoreRequest updateSugarScoreRequest = new UpdateSugarScoreRequest(orderHistory.getId(), 30);

		//when //then
		mockMvc.perform(post("/api/v1/histories/sugar-score")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateSugarScoreRequest))
						.header("access_token", ACCESS_TOKEN)
						.with(csrf()))
				.andExpect(status().isForbidden())
				.andDo(print())
				.andDo(
						document("order/내 주문 당도 갱신 실패 - 주문 작성자와 당도 평가자가 다른 경우",
								getDocumentRequest(),
								getDocumentResponse(),
								requestHeaders(
										headerWithName("access_token").description("인가 토큰")
								),
								requestFields(
										fieldWithPath("orderHistoryId").type(JsonFieldType.NUMBER).description("주문 히스토리 식별자"),
										fieldWithPath("sugarScore").type(JsonFieldType.NUMBER).description("주문에 대한 만족도 점수")
								),
								responseFields(
										fieldWithPath("message").type(JsonFieldType.STRING).description("오류 메시지"),
										fieldWithPath("path").type(JsonFieldType.STRING).description("오류 경로"),
										fieldWithPath("time").type(JsonFieldType.STRING).description("오류 발생한 시간"),
										fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("오류가 발생한 필드")
								)));
	}
}
