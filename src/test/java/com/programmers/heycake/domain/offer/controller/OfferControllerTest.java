package com.programmers.heycake.domain.offer.controller;

import static com.programmers.heycake.util.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.NotExtensible;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.ErrorCode;
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
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
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
	@DisplayName("saveOffer")
	@Transactional
	class SaveOffer {

		@Test
		@WithMockUser
		@DisplayName("Success - 제안 생성에 성공한다.")
		void saveOfferSuccess() throws Exception {
			// given
			Member writeOrderMember = getMember("writer@naver.com");
			Member marketOwnerMember = getMember("owner@naver.com");
			memberRepository.saveAll(List.of(writeOrderMember, marketOwnerMember));
			setContext(marketOwnerMember.getId(), MemberAuthority.MARKET);

			MarketEnrollment marketEnrollment = getMarketEnrollment();
			marketEnrollment.setMember(marketOwnerMember);
			marketEnrollmentRepository.save(marketEnrollment);

			Market market = getMarket();
			market.setMarketEnrollment(marketEnrollment);
			market.setMember(marketOwnerMember);
			marketRepository.save(market);

			Order order = getOrder(writeOrderMember.getId(), OrderStatus.NEW);
			orderRepository.save(order);

			OfferSaveRequest request = new OfferSaveRequest(order.getId(), 50000, "내용", getMockFile());
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
					.andDo(document("offer/제안 생성 성공",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									requestHeaders(
											headerWithName("access_token").description("Access token 정보")
									),
									requestParts(
											partWithName("offerImage").description("케이크 이미지")
									),
									requestParameters(
											parameterWithName("orderId").description("주문 id"),
											parameterWithName("expectedPrice").description("예상 가격"),
											parameterWithName("content").description("내용"),
											parameterWithName("memberId").description("글 작성자 id")
									),
									responseHeaders(
											headerWithName("Location").description("Location 헤더")
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
		@WithAnonymousUser
		@DisplayName("Fail - 사용자 인증에 실패한다.")
		void saveOfferAuthenticationFail() throws Exception {
			// given
			OfferSaveRequest request = new OfferSaveRequest(1L, 50000, "내용", getMockFile());

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
					.andDo(document("offer/제안 생성 실패 - 사용자 인증 실패",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									requestHeaders(
											headerWithName("access_token").description("Access token 정보")
									),
									requestParts(
											partWithName("offerImage").description("케이크 이미지")
									),
									requestParameters(
											parameterWithName("orderId").description("주문 id"),
											parameterWithName("expectedPrice").description("예상 가격"),
											parameterWithName("content").description("내용"),
											parameterWithName("memberId").description("글 작성자 id")
									),
									responseFields(
											fieldWithPath("message").description("실패 메세지"),
											fieldWithPath("path").description("실패 URL"),
											fieldWithPath("time").description("실패 시각"),
											fieldWithPath("inputErrors").description("입력값 검증 실패 리스트")
									)
							)
					);

			// then
			assertThat(offerRepository.findAll()).isEmpty();
			assertThat(imageRepository.findAll()).isEmpty();
		}

		@Test
		@WithMockUser
		@DisplayName("Fail - 존재하지 않는 주문인 경우 실패한다. - saveOffer")
		void saveOfferNotExistsOrderFail() throws Exception {
			// given
			Member writeOrderMember = getMember("writer@naver.com");
			Member marketOwnerMember = getMember("owner@naver.com");
			memberRepository.saveAll(List.of(writeOrderMember, marketOwnerMember));
			setContext(marketOwnerMember.getId(), MemberAuthority.MARKET);

			Order order = getOrder(writeOrderMember.getId(), OrderStatus.NEW);
			orderRepository.save(order);

			OfferSaveRequest request = new OfferSaveRequest(1L, 50000, "내용", getMockFile());
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

					.andDo(document("offer/제안 생성 실패 - 주문이 존재하지 않는 경우",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									requestHeaders(
											headerWithName("access_token").description("Access token 정보")
									),
									requestParts(
											partWithName("offerImage").description("케이크 이미지")
									),
									requestParameters(
											parameterWithName("orderId").description("주문 id"),
											parameterWithName("expectedPrice").description("예상 가격"),
											parameterWithName("content").description("내용"),
											parameterWithName("memberId").description("글 작성자 id")
									),
									responseFields(
											fieldWithPath("message").description("실패 메세지"),
											fieldWithPath("path").description("실패 URL"),
											fieldWithPath("time").description("실패 시각"),
											fieldWithPath("inputErrors").description("입력값 검증 실패 리스트")
									)
							)
					);

			// then
			assertThat(offerRepository.findAll()).isEmpty();
			assertThat(imageRepository.findAll()).isEmpty();
		}

		@Test
		@WithMockUser
		@DisplayName("Fail - 글 작성 회원이 업주가 아닌 경우 실패한다. - saveOffer")
		void saveOfferWriterIsNotMarketFail() throws Exception {
			// given
			Member writeOrderMember = getMember("writer@naver.com");
			Member normalMember = getMember("normal@naver.com");
			memberRepository.saveAll(List.of(writeOrderMember, normalMember));
			setContext(normalMember.getId(), MemberAuthority.USER);

			Order order = getOrder(writeOrderMember.getId(), OrderStatus.NEW);
			orderRepository.save(order);

			OfferSaveRequest request = new OfferSaveRequest(order.getId(), 50000, "내용", getMockFile());
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
					.andDo(document("offer/제안 생성 실패 - 글 작성 회원이 업주가 아닌 경우",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									requestHeaders(
											headerWithName("access_token").description("Access token 정보")
									),
									requestParts(
											partWithName("offerImage").description("케이크 이미지")
									),
									requestParameters(
											parameterWithName("orderId").description("주문 id"),
											parameterWithName("expectedPrice").description("예상 가격"),
											parameterWithName("content").description("내용"),
											parameterWithName("memberId").description("글 작성자 id")
									),
									responseFields(
											fieldWithPath("message").description("실패 메세지"),
											fieldWithPath("path").description("실패 URL"),
											fieldWithPath("time").description("실패 시각"),
											fieldWithPath("inputErrors").description("입력값 검증 실패 리스트")
									)
							)
					);

			// then
			assertThat(offerRepository.findAll()).isEmpty();
			assertThat(imageRepository.findAll()).isEmpty();
		}

		@Test
		@WithMockUser
		@DisplayName("Fail - 이미 제안 글을 작성하적 있는 업주인 경우 실패한다. - saveOffer")
		void saveOfferAlreadyWriteOfferFail() throws Exception {
			// given
			Member writeOrderMember = getMember("writer@naver.com");
			Member marketOwnerMember = getMember("owner@naver.com");
			memberRepository.saveAll(List.of(writeOrderMember, marketOwnerMember));
			setContext(marketOwnerMember.getId(), MemberAuthority.MARKET);

			MarketEnrollment marketEnrollment = getMarketEnrollment();
			marketEnrollment.setMember(marketOwnerMember);
			marketEnrollmentRepository.save(marketEnrollment);

			Market market = getMarket();
			market.setMarketEnrollment(marketEnrollment);
			market.setMember(marketOwnerMember);
			marketRepository.save(market);

			Order order = getOrder(writeOrderMember.getId(), OrderStatus.NEW);
			orderRepository.save(order);

			Offer alreadyExistsOffer = getOffer(market.getId(), 10000, "내용");
			alreadyExistsOffer.setOrder(order);
			offerRepository.saveAndFlush(alreadyExistsOffer);

			OfferSaveRequest request = new OfferSaveRequest(order.getId(), 50000, "내용", getMockFile());
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
					.andDo(document("offer/제안 생성 실패 - 이미 해당 글에 제안 글을 작성한 적 있는 경우",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									requestHeaders(
											headerWithName("access_token").description("Access token 정보")
									),
									requestParts(
											partWithName("offerImage").description("케이크 이미지")
									),
									requestParameters(
											parameterWithName("orderId").description("주문 id"),
											parameterWithName("expectedPrice").description("예상 가격"),
											parameterWithName("content").description("내용"),
											parameterWithName("memberId").description("글 작성자 id")
									)
							)
					);
		}

		@Test
		@WithMockUser
		@DisplayName("Fail - 픽업 날짜가 지난 주문인 경우 실패한다. - saveOffer")
		void saveOfferPassedByVisitDateFail() throws Exception {
			// given
			Member writeOrderMember = getMember("writer@naver.com");
			Member marketOwnerMember = getMember("owner@naver.com");
			memberRepository.saveAll(List.of(writeOrderMember, marketOwnerMember));
			setContext(marketOwnerMember.getId(), MemberAuthority.MARKET);

			MarketEnrollment marketEnrollment = getMarketEnrollment();
			marketEnrollment.setMember(marketOwnerMember);
			marketEnrollmentRepository.save(marketEnrollment);

			Market market = getMarket();
			market.setMarketEnrollment(marketEnrollment);
			market.setMember(marketOwnerMember);
			marketRepository.save(market);

			Order order = getOrder(writeOrderMember.getId(), OrderStatus.NEW, LocalDateTime.now().minusDays(1));
			orderRepository.save(order);

			OfferSaveRequest request = new OfferSaveRequest(order.getId(), 50000, "내용", getMockFile());
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
					.andDo(document("offer/제안 생성 실패 - 픽업 날짜가 지난 주문인 경우",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									requestHeaders(
											headerWithName("access_token").description("Access token 정보")
									),
									requestParts(
											partWithName("offerImage").description("케이크 이미지")
									),
									requestParameters(
											parameterWithName("orderId").description("주문 id"),
											parameterWithName("expectedPrice").description("예상 가격"),
											parameterWithName("content").description("내용"),
											parameterWithName("memberId").description("글 작성자 id")
									)
							)
					);

			assertThat(offerRepository.findAll()).isEmpty();
			assertThat(imageRepository.findAll()).isEmpty();
		}

		@EnumSource(value = OrderStatus.class, names = {"RESERVED", "PAID"})
		@WithMockUser
		@DisplayName("Fail - 이미 완료된 주문인 경우 실패한다. - saveOffer")
		@ParameterizedTest
		void saveOfferAlreadyDoneOrderFail(OrderStatus orderStatus) throws Exception {
			// given
			Member writeOrderMember = getMember("writer@naver.com");
			Member marketOwnerMember = getMember("owner@naver.com");
			memberRepository.saveAll(List.of(writeOrderMember, marketOwnerMember));
			setContext(marketOwnerMember.getId(), MemberAuthority.MARKET);

			MarketEnrollment marketEnrollment = getMarketEnrollment();
			marketEnrollment.setMember(marketOwnerMember);
			marketEnrollmentRepository.save(marketEnrollment);

			Market market = getMarket();
			market.setMarketEnrollment(marketEnrollment);
			market.setMember(marketOwnerMember);
			marketRepository.save(market);

			Order order = getOrder(writeOrderMember.getId(), orderStatus);
			orderRepository.save(order);

			OfferSaveRequest request = new OfferSaveRequest(order.getId(), 50000, "내용", getMockFile());
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
					.andDo(document("offer/제안 생성 실패 - 이미 완료된 주문인 경우",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									requestHeaders(
											headerWithName("access_token").description("Access token 정보")
									),
									requestParts(
											partWithName("offerImage").description("케이크 이미지")
									),
									requestParameters(
											parameterWithName("orderId").description("주문 id"),
											parameterWithName("expectedPrice").description("예상 가격"),
											parameterWithName("content").description("내용"),
											parameterWithName("memberId").description("글 작성자 id")
									)
							)
					);

			// then
			assertThat(offerRepository.findAll()).isEmpty();
			assertThat(imageRepository.findAll()).isEmpty();
		}
	}
}
