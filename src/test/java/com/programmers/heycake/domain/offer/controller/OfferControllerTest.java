package com.programmers.heycake.domain.offer.controller;

import static com.programmers.heycake.util.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.comment.repository.CommentRepository;
import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.repository.ImageRepository;
import com.programmers.heycake.domain.image.service.ImageUploadService;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.model.vo.MemberAuthority;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.offer.model.dto.request.OfferSaveRequest;
import com.programmers.heycake.domain.offer.model.dto.response.OfferSummaryResponse;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.programmers.heycake.domain.order.repository.HistoryRepository;
import com.programmers.heycake.domain.order.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
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
	ImageRepository imageRepository;

	@MockBean
	private ImageUploadService imageUploadService;

	@Autowired
	CommentRepository commentRepository;

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
		@DisplayName("Success - Offer ??? ????????????.")
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
							"offers/?????? ?????? ??????",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							pathParameters(
									parameterWithName("offerId").description("?????? ?????????")
							)
					));
		}

		@Test
		@DisplayName("Fail - Offer ?????? ??????.(BadRequest)")
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
							"offers/?????? ?????? ??????(BadRequest)",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							pathParameters(
									parameterWithName("offerId").description("?????? ?????????")
							)
					));
		}

		@Test
		@DisplayName("Fail - Offer ?????? ??????.(Unauthorized)")
		void deleteOfferUnauthorized() throws Exception {
			//given

			//when //then
			mockMvc.perform(delete("/api/v1/offers/{offerId}", 1)
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"offers/?????? ?????? ??????(Unauthorized)",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							pathParameters(
									parameterWithName("offerId").description("?????? ?????????")
							)
					));
		}

		@Test
		@DisplayName("Fail - Offer ?????? ??????.(Forbidden)")
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
							"offers/?????? ?????? ??????(Forbidden)",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							pathParameters(
									parameterWithName("offerId").description("?????? ?????????")
							)
					));
		}

		@Test
		@DisplayName("Fail - Offer ?????? ??????.(Conflict)")
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
							"offers/?????? ?????? ??????(Conflict)",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							pathParameters(
									parameterWithName("offerId").description("?????? ?????????")
							)
					));
		}
	}

	@Nested
	@DisplayName("getOffers")
	@Transactional
	class GetOffers {
	}

	@Nested
	@DisplayName("saveOffer")
	@Transactional
	class SaveOffer {

		@Test
		@DisplayName("Success - ?????? ????????? ????????????.")
		void saveOfferSuccess() throws Exception {
			// given
			Member writeOrderMember = getMember("writer@naver.com");
			Member marketOwnerMember = getMember("owner@naver.com");
			memberRepository.saveAll(List.of(writeOrderMember, marketOwnerMember));
			setContext(marketOwnerMember.getId(), MemberAuthority.MARKET);

			MarketEnrollment marketEnrollment = setTestMarketEnrollment(marketOwnerMember);

			Market market = setTestMarket(marketOwnerMember, marketEnrollment);

			Order order = getOrder(writeOrderMember.getId(), OrderStatus.NEW);
			orderRepository.save(order);

			OfferSaveRequest request = new OfferSaveRequest(order.getId(), 50000, "??????", getMockFile());
			String imageUrl = "imageURL";

			when(imageUploadService.upload(any(), any()))
					.thenReturn(imageUrl);

			Offer offerResult = getOffer(market.getId(), request.expectedPrice(), request.content());
			offerResult.setOrder(order);

			// when
			MvcResult result = mockMvc.perform(
							multipart("/api/v1/offers")
									.file("offerImage", request.offerImage().getBytes())
									.param("orderId", request.orderId().toString())
									.param("expectedPrice", String.valueOf(request.expectedPrice()))
									.param("content", request.content())
									.param("memberId", marketOwnerMember.getId().toString())
									.header("access_token", ACCESS_TOKEN)
									.contentType(MediaType.MULTIPART_FORM_DATA)
					)
					.andDo(print())
					.andExpect(status().isCreated())
					.andDo(document("offer/?????? ?????? ??????",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									requestHeaders(
											headerWithName("access_token").description("Access token ??????")
									),
									requestParts(
											partWithName("offerImage").description("????????? ?????????")
									),
									requestParameters(
											parameterWithName("orderId").description("?????? id"),
											parameterWithName("expectedPrice").description("?????? ??????"),
											parameterWithName("content").description("??????"),
											parameterWithName("memberId").description("??? ????????? id")
									),
									responseHeaders(
											headerWithName("Location").description("Location ??????")
									)
							)
					).andReturn();

			// then
			List<Offer> offers = offerRepository.findAll();
			Offer offer = offers.get(0);
			String locationValue = result.getResponse().getHeader("Location");
			List<Image> images = imageRepository.findAll();
			Image image = images.get(0);

			Image imageResult = getImage(offer.getId(), ImageType.OFFER, imageUrl);
			verify(imageUploadService).upload(any(), any());

			assertThat(offers).hasSize(1);
			assertThat(offer)
					.usingRecursiveComparison()
					.ignoringFields("id", "createdAt", "updatedAt")
					.isEqualTo(offerResult);
			assertThat(locationValue).isEqualTo("/api/v1/offers/" + offers.get(0).getId());
			assertThat(images).hasSize(1);
			assertThat(image)
					.usingRecursiveComparison()
					.ignoringFields("id")
					.isEqualTo(imageResult);
		}

		@Test
		@DisplayName("Fail - ????????? ????????? ????????????.")
		void saveOfferAuthenticationFail() throws Exception {
			// given
			OfferSaveRequest request = new OfferSaveRequest(1L, 50000, "??????", getMockFile());

			// when
			mockMvc.perform(
							multipart("/api/v1/offers")
									.file("offerImage", request.offerImage().getBytes())
									.param("orderId", request.orderId().toString())
									.param("expectedPrice", String.valueOf(request.expectedPrice()))
									.param("content", request.content())
									.param("memberId", "1")
									.header("access_token", ACCESS_TOKEN)
									.contentType(MediaType.MULTIPART_FORM_DATA)
					)
					.andDo(print())
					.andExpect(status().isUnauthorized())
					.andExpect(jsonPath("path").value("/api/v1/offers"))
					.andExpect(jsonPath("time").exists())
					.andExpect(jsonPath("inputErrors").isEmpty())
					.andDo(document("offer/?????? ?????? ?????? - ????????? ?????? ??????",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									requestHeaders(
											headerWithName("access_token").description("Access token ??????")
									),
									requestParts(
											partWithName("offerImage").description("????????? ?????????")
									),
									requestParameters(
											parameterWithName("orderId").description("?????? id"),
											parameterWithName("expectedPrice").description("?????? ??????"),
											parameterWithName("content").description("??????"),
											parameterWithName("memberId").description("??? ????????? id")
									),
									responseFields(
											fieldWithPath("message").description("?????? ?????????"),
											fieldWithPath("path").description("?????? URL"),
											fieldWithPath("time").description("?????? ??????"),
											fieldWithPath("inputErrors").description("????????? ?????? ?????? ?????????")
									)
							)
					);

			// then
			assertThat(offerRepository.findAll()).isEmpty();
			assertThat(imageRepository.findAll()).isEmpty();
		}

		@Test
		@DisplayName("Fail - ???????????? ?????? ????????? ?????? ????????????.")
		void saveOfferNotExistsOrderFail() throws Exception {
			// given
			Member writeOrderMember = getMember("writer@naver.com");
			Member marketOwnerMember = getMember("owner@naver.com");
			memberRepository.saveAll(List.of(writeOrderMember, marketOwnerMember));
			setContext(marketOwnerMember.getId(), MemberAuthority.MARKET);

			Order order = getOrder(writeOrderMember.getId(), OrderStatus.NEW);
			orderRepository.save(order);

			OfferSaveRequest request = new OfferSaveRequest(1L, 50000, "??????", getMockFile());
			String imageUrl = "imageURL";

			when(imageUploadService.upload(any(), any()))
					.thenReturn(imageUrl);

			// when
			mockMvc.perform(
							multipart("/api/v1/offers")
									.file("offerImage", request.offerImage().getBytes())
									.param("orderId", request.orderId().toString())
									.param("expectedPrice", String.valueOf(request.expectedPrice()))
									.param("content", request.content())
									.param("memberId", marketOwnerMember.getId().toString())
									.header("access_token", ACCESS_TOKEN)
									.contentType(MediaType.MULTIPART_FORM_DATA)
					)
					.andDo(print())
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("message").value(ErrorCode.ENTITY_NOT_FOUND.getMessage()))
					.andExpect(jsonPath("path").value("/api/v1/offers"))
					.andExpect(jsonPath("time").exists())
					.andExpect(jsonPath("inputErrors").isEmpty())

					.andDo(document("offer/?????? ?????? ?????? - ????????? ???????????? ?????? ??????",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									requestHeaders(
											headerWithName("access_token").description("Access token ??????")
									),
									requestParts(
											partWithName("offerImage").description("????????? ?????????")
									),
									requestParameters(
											parameterWithName("orderId").description("?????? id"),
											parameterWithName("expectedPrice").description("?????? ??????"),
											parameterWithName("content").description("??????"),
											parameterWithName("memberId").description("??? ????????? id")
									),
									responseFields(
											fieldWithPath("message").description("?????? ?????????"),
											fieldWithPath("path").description("?????? URL"),
											fieldWithPath("time").description("?????? ??????"),
											fieldWithPath("inputErrors").description("????????? ?????? ?????? ?????????")
									)
							)
					);

			// then
			assertThat(offerRepository.findAll()).isEmpty();
			assertThat(imageRepository.findAll()).isEmpty();
		}

		@Test
		@DisplayName("Fail - ??? ?????? ????????? ????????? ?????? ?????? ????????????.")
		void saveOfferWriterIsNotMarketFail() throws Exception {
			// given
			Member writeOrderMember = getMember("writer@naver.com");
			Member normalMember = getMember("normal@naver.com");
			memberRepository.saveAll(List.of(writeOrderMember, normalMember));
			setContext(normalMember.getId(), MemberAuthority.USER);

			Order order = getOrder(writeOrderMember.getId(), OrderStatus.NEW);
			orderRepository.save(order);

			OfferSaveRequest request = new OfferSaveRequest(order.getId(), 50000, "??????", getMockFile());
			String imageUrl = "imageURL";

			when(imageUploadService.upload(any(), any()))
					.thenReturn(imageUrl);

			// when
			mockMvc.perform(
							multipart("/api/v1/offers")
									.file("offerImage", request.offerImage().getBytes())
									.param("orderId", request.orderId().toString())
									.param("expectedPrice", String.valueOf(request.expectedPrice()))
									.param("content", request.content())
									.param("memberId", normalMember.getId().toString())
									.header("access_token", ACCESS_TOKEN)
									.contentType(MediaType.MULTIPART_FORM_DATA)
					)
					.andDo(print())
					.andExpect(status().isForbidden())
					.andExpect(jsonPath("path").value("/api/v1/offers"))
					.andExpect(jsonPath("time").exists())
					.andExpect(jsonPath("inputErrors").isEmpty())
					.andDo(document("offer/?????? ?????? ?????? - ??? ?????? ????????? ????????? ?????? ??????",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									requestHeaders(
											headerWithName("access_token").description("Access token ??????")
									),
									requestParts(
											partWithName("offerImage").description("????????? ?????????")
									),
									requestParameters(
											parameterWithName("orderId").description("?????? id"),
											parameterWithName("expectedPrice").description("?????? ??????"),
											parameterWithName("content").description("??????"),
											parameterWithName("memberId").description("??? ????????? id")
									),
									responseFields(
											fieldWithPath("message").description("?????? ?????????"),
											fieldWithPath("path").description("?????? URL"),
											fieldWithPath("time").description("?????? ??????"),
											fieldWithPath("inputErrors").description("????????? ?????? ?????? ?????????")
									)
							)
					);

			// then
			assertThat(offerRepository.findAll()).isEmpty();
			assertThat(imageRepository.findAll()).isEmpty();
		}

		@Test
		@DisplayName("Success - Offer ?????? ????????? ????????????.")
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
					.andDo(document("offer/?????? ?????? ??????",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									pathParameters(
											parameterWithName("orderId").description("?????? id")
									),
									responseFields(
											fieldWithPath("[]").description("?????? ??????"),
											fieldWithPath("[].offerId").description("?????? id"),
											fieldWithPath("[].marketId").description("????????? ????????? ?????? id"),
											fieldWithPath("[].enrollmentId").description("?????? ?????? id"),
											fieldWithPath("[].marketName").description("?????? ??????"),
											fieldWithPath("[].expectedPrice").description("?????? ??????"),
											fieldWithPath("[].createdDate").description("?????? ?????? ??????"),
											fieldWithPath("[].isPaid").description("?????? ?????? ??????"),
											fieldWithPath("[].imageUrl").description("?????? ????????? URL"),
											fieldWithPath("[].content").description("?????? ??????"),
											fieldWithPath("[].commentCount").description("????????? ?????? ?????? ??????")
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
						.isEqualTo(offersSuccessResponses.get(responseBodyIdx)
								.createdDate()
								.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
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
		@DisplayName("Fail - ???????????? ?????? ????????? ?????? ????????????.")
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
							document("offer/?????? ?????? ?????? - ???????????? ?????? ????????? ??????",
									pathParameters(
											parameterWithName("orderId").description("?????? id")
									),
									responseFields(
											fieldWithPath("message").description("?????? ?????????"),
											fieldWithPath("path").description("?????? URL"),
											fieldWithPath("time").description("?????? ??????"),
											fieldWithPath("inputErrors").description("????????? ?????? ?????? ?????????")
									)
							)
					);
		}
	}

	@Test
	@DisplayName("Fail - ?????? ?????? ?????? ???????????? ?????? ????????? ?????? ????????????.")
	void saveOfferAlreadyWriteOfferFail() throws Exception {
		// given
		Member writeOrderMember = getMember("writer@naver.com");
		Member marketOwnerMember = getMember("owner@naver.com");
		memberRepository.saveAll(List.of(writeOrderMember, marketOwnerMember));
		setContext(marketOwnerMember.getId(), MemberAuthority.MARKET);

		MarketEnrollment marketEnrollment = setTestMarketEnrollment(marketOwnerMember);

		Market market = setTestMarket(marketOwnerMember, marketEnrollment);

		Order order = getOrder(writeOrderMember.getId(), OrderStatus.NEW);
		orderRepository.save(order);

		Offer alreadyExistsOffer = getOffer(market.getId(), 10000, "??????");
		alreadyExistsOffer.setOrder(order);
		offerRepository.saveAndFlush(alreadyExistsOffer);

		OfferSaveRequest request = new OfferSaveRequest(order.getId(), 50000, "??????", getMockFile());
		String imageUrl = "imageURL";

		when(imageUploadService.upload(any(), any()))
				.thenReturn(imageUrl);

		Offer offerResult = getOffer(market.getId(), request.expectedPrice(), request.content());
		offerResult.setOrder(order);

		// when
		mockMvc.perform(
						multipart("/api/v1/offers")
								.file("offerImage", request.offerImage().getBytes())
								.param("orderId", request.orderId().toString())
								.param("expectedPrice", String.valueOf(request.expectedPrice()))
								.param("content", request.content())
								.param("memberId", marketOwnerMember.getId().toString())
								.header("access_token", ACCESS_TOKEN)
								.contentType(MediaType.MULTIPART_FORM_DATA)
				)
				.andDo(print())
				.andExpect(status().isConflict())
				.andExpect(jsonPath("message").value(ErrorCode.DUPLICATED_OFFER.getMessage()))
				.andExpect(jsonPath("path").value("/api/v1/offers"))
				.andExpect(jsonPath("time").exists())
				.andExpect(jsonPath("inputErrors").isEmpty())
				.andDo(document("offer/?????? ?????? ?????? - ?????? ?????? ?????? ?????? ?????? ????????? ??? ?????? ??????",
								preprocessRequest(prettyPrint()),
								preprocessResponse(prettyPrint()),
								requestHeaders(
										headerWithName("access_token").description("Access token ??????")
								),
								requestParts(
										partWithName("offerImage").description("????????? ?????????")
								),
								requestParameters(
										parameterWithName("orderId").description("?????? id"),
										parameterWithName("expectedPrice").description("?????? ??????"),
										parameterWithName("content").description("??????"),
										parameterWithName("memberId").description("??? ????????? id")
								)
						)
				);
	}

	@Test
	@WithMockUser
	@DisplayName("Fail - ?????? ????????? ?????? ????????? ?????? ????????????.")
	void saveOfferPassedByVisitDateFail() throws Exception {
		// given
		Member writeOrderMember = getMember("writer@naver.com");
		Member marketOwnerMember = getMember("owner@naver.com");
		memberRepository.saveAll(List.of(writeOrderMember, marketOwnerMember));
		setContext(marketOwnerMember.getId(), MemberAuthority.MARKET);

		MarketEnrollment marketEnrollment = setTestMarketEnrollment(marketOwnerMember);

		Market market = setTestMarket(marketOwnerMember, marketEnrollment);

		Order order = getOrder(writeOrderMember.getId(), OrderStatus.NEW, LocalDateTime.now().minusDays(1));
		orderRepository.save(order);

		OfferSaveRequest request = new OfferSaveRequest(order.getId(), 50000, "??????", getMockFile());
		String imageUrl = "imageURL";

		when(imageUploadService.upload(any(), any()))
				.thenReturn(imageUrl);

		Offer offerResult = getOffer(market.getId(), request.expectedPrice(), request.content());
		offerResult.setOrder(order);

		// when, then
		mockMvc.perform(
						multipart("/api/v1/offers")
								.file("offerImage", request.offerImage().getBytes())
								.param("orderId", request.orderId().toString())
								.param("expectedPrice", String.valueOf(request.expectedPrice()))
								.param("content", request.content())
								.param("memberId", marketOwnerMember.getId().toString())
								.header("access_token", ACCESS_TOKEN)
								.contentType(MediaType.MULTIPART_FORM_DATA)
				)
				.andDo(print())
				.andExpect(status().isConflict())
				.andExpect(jsonPath("message").value(ErrorCode.VISIT_DATE_PASSED.getMessage()))
				.andExpect(jsonPath("path").value("/api/v1/offers"))
				.andExpect(jsonPath("time").exists())
				.andExpect(jsonPath("inputErrors").isEmpty())
				.andDo(document("offer/?????? ?????? ?????? - ?????? ????????? ?????? ????????? ??????",
								preprocessRequest(prettyPrint()),
								preprocessResponse(prettyPrint()),
								requestHeaders(
										headerWithName("access_token").description("Access token ??????")
								),
								requestParts(
										partWithName("offerImage").description("????????? ?????????")
								),
								requestParameters(
										parameterWithName("orderId").description("?????? id"),
										parameterWithName("expectedPrice").description("?????? ??????"),
										parameterWithName("content").description("??????"),
										parameterWithName("memberId").description("??? ????????? id")
								)
						)
				);

		assertThat(offerRepository.findAll()).isEmpty();
		assertThat(imageRepository.findAll()).isEmpty();
	}

	@EnumSource(value = OrderStatus.class, names = {"RESERVED", "PAID"})
	@DisplayName("Fail - ?????? ????????? ????????? ?????? ????????????.")
	@ParameterizedTest
	void saveOfferAlreadyDoneOrderFail(OrderStatus orderStatus) throws Exception {
		// given
		Member writeOrderMember = getMember("writer@naver.com");
		Member marketOwnerMember = getMember("owner@naver.com");
		memberRepository.saveAll(List.of(writeOrderMember, marketOwnerMember));
		setContext(marketOwnerMember.getId(), MemberAuthority.MARKET);

		MarketEnrollment marketEnrollment = setTestMarketEnrollment(marketOwnerMember);

		Market market = setTestMarket(marketOwnerMember, marketEnrollment);

		Order order = getOrder(writeOrderMember.getId(), orderStatus);
		orderRepository.save(order);

		OfferSaveRequest request = new OfferSaveRequest(order.getId(), 50000, "??????", getMockFile());
		String imageUrl = "imageURL";

		when(imageUploadService.upload(any(), any()))
				.thenReturn(imageUrl);

		Offer offerResult = getOffer(market.getId(), request.expectedPrice(), request.content());
		offerResult.setOrder(order);

		// when
		mockMvc.perform(
						multipart("/api/v1/offers")
								.file("offerImage", request.offerImage().getBytes())
								.param("orderId", request.orderId().toString())
								.param("expectedPrice", String.valueOf(request.expectedPrice()))
								.param("content", request.content())
								.param("memberId", marketOwnerMember.getId().toString())
								.header("access_token", ACCESS_TOKEN)
								.contentType(MediaType.MULTIPART_FORM_DATA)
				)
				.andDo(print())
				.andExpect(status().isConflict())
				.andExpect(jsonPath("message").value(ErrorCode.ORDER_CLOSED.getMessage()))
				.andExpect(jsonPath("path").value("/api/v1/offers"))
				.andExpect(jsonPath("time").exists())
				.andExpect(jsonPath("inputErrors").isEmpty())
				.andDo(document("offer/?????? ?????? ?????? - ?????? ????????? ????????? ??????",
								preprocessRequest(prettyPrint()),
								preprocessResponse(prettyPrint()),
								requestHeaders(
										headerWithName("access_token").description("Access token ??????")
								),
								requestParts(
										partWithName("offerImage").description("????????? ?????????")
								),
								requestParameters(
										parameterWithName("orderId").description("?????? id"),
										parameterWithName("expectedPrice").description("?????? ??????"),
										parameterWithName("content").description("??????"),
										parameterWithName("memberId").description("??? ????????? id")
								)
						)
				);

		// then
		assertThat(offerRepository.findAll()).isEmpty();
		assertThat(imageRepository.findAll()).isEmpty();
	}
}
