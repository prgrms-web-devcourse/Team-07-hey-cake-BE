package com.programmers.heycake.domain.offer.controller;

import static com.programmers.heycake.domain.market.model.entity.QMarket.*;
import static com.programmers.heycake.util.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.comment.repository.CommentRepository;
import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.repository.ImageRepository;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.model.vo.MemberAuthority;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.offer.model.dto.response.OfferSummaryResponse;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.programmers.heycake.domain.order.repository.HistoryRepository;
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

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	ImageRepository imageRepository;

	@Autowired
	HistoryRepository historyRepository;

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

	@Nested
	@DisplayName("getOffers")
	@Transactional
	class GetOffers {

		@Test
		@DisplayName("Success - Offer 목록 조회에 성공한다.")
		void getOffersSuccess() throws Exception {

			// given
			Member member1 = getMember("member1@email.com");
			Member member2 = getMember("member2@email.com");
			memberRepository.saveAll(List.of(member1, member2));

			MarketEnrollment marketEnrollment1 = getMarketEnrollment("0000000000", member1);
			MarketEnrollment marketEnrollment2 = getMarketEnrollment("0000000001", member2);
			marketEnrollmentRepository.saveAll(List.of(marketEnrollment1, marketEnrollment2));

			Market market1 = getMarket("01011111111", member1, marketEnrollment1);
			Market market2 = getMarket("01022222222", member2, marketEnrollment2);
			marketRepository.saveAll(List.of(market1, market2));

			Order order = getOrder(0L);
			orderRepository.save(order);

			Offer offer1 = getOffer(market1.getId(), 10000, "content1", order);
			Offer offer2 = getOffer(market2.getId(), 20000, "content2", order);
			offerRepository.saveAll(List.of(offer1, offer2));

			Comment comment1OnOffer1 = getComment(member1.getId(), offer1);
			Comment comment2ByOffer1 = getComment(member1.getId(), offer1);
			Comment comment1ByOffer2 = getComment(member2.getId(), offer2);
			commentRepository.saveAll(List.of(comment1OnOffer1, comment2ByOffer1, comment2ByOffer1));

			Image image1 = getImage(offer1.getId(), ImageType.OFFER, "offerImageUrl1");
			Image image2 = getImage(offer2.getId(), ImageType.OFFER, "offerImageUrl2");
			imageRepository.saveAll(List.of(image1, image2));

			OrderHistory orderHistory = getOrderHistory(order.getMemberId(), market1.getId(), order);
			historyRepository.save(orderHistory);

			List<OfferSummaryResponse> offersSuccessResponses = List.of(
					getOffersSuccessResponses(offer1, market1, marketEnrollment1, image1, true, 2),
					getOffersSuccessResponses(offer2, market2, marketEnrollment2, image2, false, 1)
			);

			// when
			MvcResult mvcResult = mockMvc.perform(
							get("/api/v1/orders/{orderId}/offers", order.getId()))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$").isArray())
					.andDo(document("offer/오퍼 조회 성공",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									pathParameters(
											parameterWithName("orderId").description("주문 id")
									),
									responseFields(
											fieldWithPath("[]").description("오퍼 목록"),
											fieldWithPath("[].offerId").description("오퍼 id"),
											fieldWithPath("[].marketId").description("오퍼를 작성한 마켓 id"),
											fieldWithPath("[].enrollmentId").description("마켓 등록 id"),
											fieldWithPath("[].marketName").description("마켓 이름"),
											fieldWithPath("[].expectedPrice").description("희망 가격"),
											fieldWithPath("[].createdDate").description("오퍼 작성 날짜"),
											fieldWithPath("[].isPaid").description("오퍼 결제 여부"),
											fieldWithPath("[].imageUrl").description("오퍼 이미지 URL"),
											fieldWithPath("[].content").description("오퍼 내용"),
											fieldWithPath("[].commentCount").description("오퍼에 달린 댓글 개수")
									)
							)
					).andReturn();

			// then
			JSONArray responseBody = new JSONArray(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8));
			for (int responseBodyIdx = 0; responseBodyIdx < offersSuccessResponses.size(); responseBodyIdx++) {
				JSONObject responseResult = responseBody.getJSONObject(responseBodyIdx);

				assertThat(responseResult.getLong("offerId"))
						.isEqualTo(offersSuccessResponses.get(responseBodyIdx).offerId());
				assertThat(responseResult.getLong("marketId"))
						.isEqualTo(offersSuccessResponses.get(responseBodyIdx).marketId());
				assertThat(responseResult.getLong("enrollmentId"))
						.isEqualTo(offersSuccessResponses.get(responseBodyIdx).enrollmentId());
				assertThat(responseResult.getString("marketName"))
						.isEqualTo(offersSuccessResponses.get(responseBodyIdx).marketName());
				assertThat(responseResult.getInt("expectedPrice"))
						.isEqualTo(offersSuccessResponses.get(responseBodyIdx).expectedPrice());
				assertThat(responseResult.getString("createdDate"))
						.isEqualTo(offersSuccessResponses.get(responseBodyIdx).createdDate().toString());
				assertThat(responseResult.getBoolean("isPaid"))
						.isEqualTo(offersSuccessResponses.get(responseBodyIdx).isPaid());
				assertThat(responseResult.getString("imageUrl"))
						.isEqualTo(offersSuccessResponses.get(responseBodyIdx).imageUrl());
				assertThat(responseResult.getString("content"))
						.isEqualTo(offersSuccessResponses.get(responseBodyIdx).content());
				assertThat(responseResult.getInt("commentCount"))
						.isEqualTo(offersSuccessResponses.get(responseBodyIdx).commentCount());
			}
		}

		private OfferSummaryResponse getOffersSuccessResponses(Offer offer, Market market,
				MarketEnrollment marketEnrollment,
				Image image, boolean isPaid, int commentCount) {

			return OfferSummaryResponse.builder()
					.offerId(offer.getId())
					.marketId(market.getId())
					.enrollmentId(marketEnrollment.getId())
					.marketName(marketEnrollment.getMarketName())
					.expectedPrice(offer.getExpectedPrice())
					.createdDate(offer.getCreatedAt().toLocalDate())
					.isPaid(isPaid)
					.imageUrl(image.getImageUrl())
					.content(offer.getContent())
					.commentCount(commentCount)
					.build();
		}

		@Test
		@DisplayName("Fail - 존재하지 않는 주문인 경우 실패한다.")
		void getOffersNotExistsOrderFail() throws Exception {
			// given
			Long notExistsOrderId = 0L;

			// when, then
			mockMvc.perform(
							get("/api/v1/orders/{orderId}/offers", notExistsOrderId))
					.andDo(print())
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("message").value(ErrorCode.ENTITY_NOT_FOUND.getMessage()))
					.andExpect(jsonPath("path").value("/api/v1/orders/" + notExistsOrderId + "/offers"))
					.andExpect(jsonPath("time").exists())
					.andExpect(jsonPath("inputErrors").isEmpty())
					.andDo(
							document("offer/오퍼 조회 실패 - 존재하지 않는 오퍼인 경우",
									pathParameters(
											parameterWithName("orderId").description("주문 id")
									),
									responseFields(
											fieldWithPath("message").description("실패 메세지"),
											fieldWithPath("path").description("실패 URL"),
											fieldWithPath("time").description("실패 시각"),
											fieldWithPath("inputErrors").description("입력값 검증 실패 리스트")
									)
							)
					);
		}
	}

}
