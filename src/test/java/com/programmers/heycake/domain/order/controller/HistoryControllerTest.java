package com.programmers.heycake.domain.order.controller;

import static com.programmers.heycake.util.TestUtils.*;
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
import com.programmers.heycake.domain.order.model.dto.request.HistoryControllerRequest;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.repository.OrderRepository;

@Transactional
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class HistoryControllerTest {

	private static final String ACCESS_TOKEN = "access_token";

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
	MarketEnrollmentRepository marketEnrollmentRepository;

	private Offer setTestOffer(Order order, Market market) {
		Offer offer = getOffer(market.getId(), 1000, "content");
		offer.setOrder(order);
		offerRepository.save(offer);
		return offer;
	}

	private Market setTestMarket(Member member, MarketEnrollment marketEnrollment) {
		Market market = getMarket();
		market.setMember(member);
		market.setMarketEnrollment(marketEnrollment);
		marketRepository.save(market);
		return market;
	}

	private MarketEnrollment setTestMarketEnrollment(Member member, String businessNumber) {
		MarketEnrollment marketEnrollment = getMarketEnrollment(businessNumber);
		marketEnrollment.setMember(member);
		marketEnrollmentRepository.save(marketEnrollment);
		return marketEnrollment;
	}

	@Nested
	@DisplayName("createHistory")
	@Transactional
	class CreateHistory {
		@Test
		@DisplayName("Success - orderHistory ??? ????????????.")
		void createHistorySuccess() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), MemberAuthority.USER);

			Order order = orderRepository.save(getOrder(member.getId()));

			MarketEnrollment marketEnrollment = setTestMarketEnrollment(member, "1231231234");
			Market market = setTestMarket(member, marketEnrollment);

			Offer offer = setTestOffer(order, market);

			HistoryControllerRequest historyControllerRequest =
					new HistoryControllerRequest(order.getId(), offer.getId(), true);

			//when //then
			mockMvc.perform(post("/api/v1/histories")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(historyControllerRequest))
							.header("access_token", ACCESS_TOKEN)
							.with(csrf()))
					.andExpect(status().isCreated())
					.andDo(print())
					.andDo(
							document("histories/?????? ?????? ?????? ??????",
									requestHeaders(
											headerWithName("access_token").description("?????? ??????")
									),
									requestFields(
											fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
											fieldWithPath("offerId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
											fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("?????? ??????")
									),
									responseHeaders(
											headerWithName("location").description("?????? ??????")
									)
							));
		}

		@Test
		@DisplayName("Fail - orderHistory ?????? ??????.(BadRequest)")
		void createHistoryBadRequest() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			Member anotherMember = memberRepository.save(getMember("anotherMember"));
			setContext(member.getId(), MemberAuthority.USER);

			Order order = orderRepository.save(getOrder(member.getId()));

			MarketEnrollment marketEnrollment = setTestMarketEnrollment(anotherMember, "1234123412");
			Market market = setTestMarket(member, marketEnrollment);

			setTestOffer(order, market);

			Order anotherOrder = orderRepository.save(getOrder(anotherMember.getId()));

			MarketEnrollment anotherMarketEnrollment = setTestMarketEnrollment(anotherMember, "1231231234");
			Market anotherMarket = setTestMarket(anotherMember, anotherMarketEnrollment);

			Offer anotherOffer = setTestOffer(anotherOrder, anotherMarket);

			HistoryControllerRequest historyControllerRequest =
					new HistoryControllerRequest(order.getId(), anotherOffer.getId(), true);

			//when //then
			mockMvc.perform(post("/api/v1/histories")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(historyControllerRequest))
							.header("access_token", ACCESS_TOKEN)
							.with(csrf()))
					.andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(
							document("histories/?????? ?????? ?????? ??????(BadRequest)",
									requestHeaders(
											headerWithName("access_token").description("?????? ??????")
									),
									requestFields(
											fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
											fieldWithPath("offerId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
											fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("?????? ??????")
									),
									responseFields(
											fieldWithPath("message").description("?????? ?????????"),
											fieldWithPath("path").description("?????? ?????? uri"),
											fieldWithPath("time").description("?????? ?????? ??????"),
											fieldWithPath("inputErrors").description("?????? ??????")
									)
							));
		}

		@Test
		@DisplayName("Fail - orderHistory ????????????.(Unauthorized)")
		void createHistoryUnauthorized() throws Exception {
			//given
			HistoryControllerRequest historyControllerRequest = new HistoryControllerRequest(2L, 2L, true);

			//when //then
			mockMvc.perform(post("/api/v1/histories")
							.header("access_token", ACCESS_TOKEN)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(historyControllerRequest))
							.with(csrf()))
					.andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(
							document("histories/?????? ?????? ?????? ??????(Unauthorized)",
									requestHeaders(
											headerWithName("access_token").description("?????? ??????")
									),
									requestFields(
											fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
											fieldWithPath("offerId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
											fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("?????? ??????")
									)
							));
		}

		@Test
		@DisplayName("Fail - orderHistory ????????????.(Forbidden)")
		void createHistoryForbidden() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			Member anotherMember = memberRepository.save(getMember("anotherMember"));
			setContext(member.getId(), MemberAuthority.USER);

			Order order = orderRepository.save(getOrder(member.getId()));

			MarketEnrollment marketEnrollment = setTestMarketEnrollment(anotherMember, "1234123412");
			Market market = setTestMarket(member, marketEnrollment);

			setTestOffer(order, market);

			Order anotherOrder = orderRepository.save(getOrder(anotherMember.getId()));

			MarketEnrollment anotherMarketEnrollment = setTestMarketEnrollment(anotherMember, "1231231231");
			Market anotherMarket = setTestMarket(anotherMember, anotherMarketEnrollment);

			Offer anotherOffer = setTestOffer(anotherOrder, anotherMarket);

			HistoryControllerRequest historyControllerRequest = new HistoryControllerRequest(anotherOrder.getId(),
					anotherOffer.getId(), true);

			//when //then
			mockMvc.perform(post("/api/v1/histories")
							.header("access_token", ACCESS_TOKEN)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(historyControllerRequest))
							.with(csrf()))
					.andExpect(status().isForbidden())
					.andDo(print())
					.andDo(
							document("histories/?????? ?????? ?????? ??????(Forbidden)",
									requestHeaders(
											headerWithName("access_token").description("?????? ??????")
									),
									requestFields(
											fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
											fieldWithPath("offerId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
											fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("?????? ??????")
									)
							));
		}

		@Test
		@DisplayName("Fail - orderHistory ????????????.(NotFound)")
		void createHistoryNotFound() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), MemberAuthority.USER);

			HistoryControllerRequest historyControllerRequest =
					new HistoryControllerRequest(0L, 0L, true);

			//when //then
			mockMvc.perform(post("/api/v1/histories")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(historyControllerRequest))
							.header("access_token", ACCESS_TOKEN)
							.with(csrf()))
					.andExpect(status().isNotFound())
					.andDo(print())
					.andDo(
							document("histories/?????? ?????? ?????? ??????(NotFound)",
									requestHeaders(
											headerWithName("access_token").description("?????? ??????")
									),
									requestFields(
											fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
											fieldWithPath("offerId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
											fieldWithPath("isPaid").type(JsonFieldType.BOOLEAN).description("?????? ??????")
									),
									responseFields(
											fieldWithPath("message").description("?????? ?????????"),
											fieldWithPath("path").description("?????? ?????? uri"),
											fieldWithPath("time").description("?????? ?????? ??????"),
											fieldWithPath("inputErrors").description("?????? ??????")
									)
							));
		}

	}

}
