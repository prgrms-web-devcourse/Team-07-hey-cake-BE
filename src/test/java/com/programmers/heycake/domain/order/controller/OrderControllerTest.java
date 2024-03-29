package com.programmers.heycake.domain.order.controller;

import static com.programmers.heycake.common.util.ApiDocumentUtils.*;
import static com.programmers.heycake.common.util.TestUtils.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;
import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.*;
import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.MARKET;
import static com.programmers.heycake.domain.order.model.vo.CakeCategory.*;
import static com.programmers.heycake.domain.order.model.vo.OrderStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.programmers.heycake.common.util.TestUtils;
import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.repository.ImageRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.model.vo.MemberAuthority;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.vo.BreadFlavor;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
import com.programmers.heycake.domain.order.model.vo.CakeHeight;
import com.programmers.heycake.domain.order.model.vo.CakeSize;
import com.programmers.heycake.domain.order.model.vo.CreamFlavor;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.programmers.heycake.domain.order.repository.OrderRepository;

@SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class OrderControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	OfferRepository offerRepository;

	@Autowired
	private ImageRepository imageRepository;

	private static final String ACCESS_TOKEN = "access_token";
	private static final String INVALID_ACCESS_TOKEN = "access_token";

	void setOrders(Member member, int amount) {
		for (int i = 0; i < amount; i++) {
			orderRepository.save(getOrder(member.getId()));
		}
	}

	@Nested
	@DisplayName("getMyOrderList")
	@Transactional
	class GetMyOrder {
		@Test
		@DisplayName("Success - getMyOrderList 조회한다.")
		void getMyOrderListSuccess() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), USER);
			setOrders(member, 10);

			//when //then
			mockMvc.perform(get("/api/v1/orders/my?cursorId=1&pageSize=10&orderStatus=NEW")
							.header("access_token", ACCESS_TOKEN)
					)
					.andExpect(status().isOk())
					.andDo(print())
					.andDo(document(
							"order/내 주문 목록 조회 성공",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							requestParameters(
									parameterWithName("cursorId").description("커서 주문 식별자"),
									parameterWithName("pageSize").description("페이지 크기"),
									parameterWithName("orderStatus").description("주문 상태")
							),
							responseFields(
									fieldWithPath("myOrdersResponse").description("주문 목록"),
									fieldWithPath("myOrdersResponse[].id").description("주문 식별자"),
									fieldWithPath("myOrdersResponse[].title").description("주문 제목"),
									fieldWithPath("myOrdersResponse[].orderStatus").description("주문 상태"),
									fieldWithPath("myOrdersResponse[].region").description("주문 지역"),
									fieldWithPath("myOrdersResponse[].visitTime").description("방문 시간"),
									fieldWithPath("myOrdersResponse[].createdAt").description("생성 시간"),
									fieldWithPath("myOrdersResponse[].cakeInfo").description("생성 시간"),
									fieldWithPath("myOrdersResponse[].cakeInfo.cakeCategory").description("케익 종류"),
									fieldWithPath("myOrdersResponse[].cakeInfo.cakeSize").description("케익 크기"),
									fieldWithPath("myOrdersResponse[].cakeInfo.cakeHeight").description("케익 높이"),
									fieldWithPath("myOrdersResponse[].cakeInfo.breadFlavor").description("빵 맛"),
									fieldWithPath("myOrdersResponse[].cakeInfo.creamFlavor").description("크림 맛"),
									fieldWithPath("myOrdersResponse[].cakeInfo.requirements").description("요청 사항"),
									fieldWithPath("myOrdersResponse[].hopePrice").description("희망 가격"),
									fieldWithPath("myOrdersResponse[].imageUrl").description("이미지 주소"),
									fieldWithPath("myOrdersResponse[].offerCount").description("오퍼 갯수"),
									fieldWithPath("cursorId").description("커서 식별자")
							)));
		}

		@Test
		@DisplayName("Fail - getMyOrderList 조회 실패.(BadRequest)")
		void getMyOrderListBadRequest() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));

			setContext(member.getId(), USER);
			setOrders(member, 10);

			//when //then
			mockMvc.perform(get("/api/v1/orders/my?cursorId=1&pageSize=10&orderStatus=ADSDF")
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document(
							"order/내 주문 목록 조회 실패(BadRequest)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							requestParameters(
									parameterWithName("cursorId").description("커서 주문 식별자"),
									parameterWithName("pageSize").description("페이지 크기"),
									parameterWithName("orderStatus").description("주문 상태")
							),
							responseFields(
									fieldWithPath("message").description("에러 메세지"),
									fieldWithPath("path").description("에러 발생 uri"),
									fieldWithPath("time").description("에러 발생 시각"),
									fieldWithPath("inputErrors").description("에러 상세"),
									fieldWithPath("inputErrors[].field").description("에러 필드"),
									fieldWithPath("inputErrors[].rejectedValue").description("에러 값"),
									fieldWithPath("inputErrors[].message").description("상세 메세지")
							)
					));
		}

		@Test
		@DisplayName("Fail - getMyOrderList 조회 실패.(Unauthorized)")
		void getMyOrderListUnauthorized() throws Exception {
			//given

			//when //then
			mockMvc.perform(get("/api/v1/orders/my?cursorId=1&pageSize=10&orderStatus=NEW")
							.header("access_token", INVALID_ACCESS_TOKEN)
					).andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"order/내 주문 목록 조회 실패(Unauthorized)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							requestParameters(
									parameterWithName("cursorId").description("커서 주문 식별자"),
									parameterWithName("pageSize").description("페이지 크기"),
									parameterWithName("orderStatus").description("주문 상태")
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
		@WithAnonymousUser
		@DisplayName("Fail - getMyOrderList 조회 실패.(Forbidden)")
		void getMyOrderListForbidden() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), MemberAuthority.ADMIN);

			//when //then
			mockMvc.perform(get("/api/v1/orders/my?cursorId=1&pageSize=10&orderStatus=NEW")
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document(
							"order/내 주문 목록 조회 실패(Forbidden)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							requestParameters(
									parameterWithName("cursorId").description("커서 주문 식별자"),
									parameterWithName("pageSize").description("페이지 크기"),
									parameterWithName("orderStatus").description("주문 상태")
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
	@DisplayName("getOrders")
	@Transactional
	class GetOrders {
		@Test
		@DisplayName("Success - Order 리스트 조회 성공해서 200으로 응답한다.")
		@Transactional
		void getOrdersSuccess() throws Exception {

			orderRepository.saveAll(
					List.of(
							TestUtils.getOrder(1L, PHOTO, NEW, "강남구", LocalDateTime.now().plusDays(1)),
							TestUtils.getOrder(1L, PHOTO, NEW, "강남구", LocalDateTime.now().plusDays(1)),
							TestUtils.getOrder(1L, PHOTO, NEW, "강남구", LocalDateTime.now().minusDays(1)),
							TestUtils.getOrder(1L, PHOTO, NEW, "강남구", LocalDateTime.now().minusDays(1)),
							TestUtils.getOrder(1L, PHOTO, PAID, "강남구", LocalDateTime.now().plusDays(1)),
							TestUtils.getOrder(1L, PHOTO, PAID, "강남구", LocalDateTime.now().plusDays(1)),
							TestUtils.getOrder(1L, LETTERING, NEW, "강남구", LocalDateTime.now().plusDays(1)),
							TestUtils.getOrder(1L, LETTERING, NEW, "강남구", LocalDateTime.now().plusDays(1)),
							TestUtils.getOrder(1L, LETTERING, NEW, "강남구", LocalDateTime.now().plusDays(1)),
							TestUtils.getOrder(1L, PHOTO, NEW, "강동구", LocalDateTime.now().plusDays(1)),
							TestUtils.getOrder(1L, PHOTO, NEW, "강동구", LocalDateTime.now().plusDays(1)),
							TestUtils.getOrder(1L, PHOTO, NEW, "강동구", LocalDateTime.now().plusDays(1))
					)
			);

			mockMvc.perform(get("/api/v1/orders")
							.param("pageSize", "5")
							.param("cakeCategory", "PHOTO")
							.param("orderStatus", "NEW")
							.param("region", "강남구")
					)
					.andExpect(status().isOk())
					.andDo(print())
					.andExpect(jsonPath("$.content[0].orderStatus").value("NEW"))
					.andExpect(jsonPath("$.content[0].cakeInfo.cakeCategory").value("PHOTO"))
					.andExpect(jsonPath("$.content[0].region").value("강남구"))
					.andExpect(jsonPath("$.content[1].orderStatus").value("NEW"))
					.andExpect(jsonPath("$.content[1].cakeInfo.cakeCategory").value("PHOTO"))
					.andExpect(jsonPath("$.content[1].region").value("강남구"))
					.andDo(document("order/주문 목록 조회 성공",
							requestParameters(
									parameterWithName("pageSize").description("페이지 사이즈"),
									parameterWithName("cakeCategory").description("케익 분류"),
									parameterWithName("orderStatus").description("주문 상태"),
									parameterWithName("region").description("희망 지역")
							),
							responseFields(
									fieldWithPath("content").type(JsonFieldType.ARRAY).description("주문 리스트"),
									fieldWithPath("cursorId").type(JsonFieldType.NUMBER).description("커서"),
									fieldWithPath("isLast").type(JsonFieldType.BOOLEAN).description("마지막 페이지"),
									fieldWithPath("content[].orderId").type(JsonFieldType.NUMBER).description("주문 아이디"),
									fieldWithPath("content[].title").type(JsonFieldType.STRING).description("주문 제목"),
									fieldWithPath("content[].region").type(JsonFieldType.STRING).description("희망 지역"),
									fieldWithPath("content[].orderStatus").type(JsonFieldType.STRING).description("주문 상태"),
									fieldWithPath("content[].hopePrice").type(JsonFieldType.NUMBER).description("희망 가격"),
									fieldWithPath("content[].visitTime").type(JsonFieldType.STRING).description("희망 방문 시간"),
									fieldWithPath("content[].cakeInfo").type(JsonFieldType.OBJECT).description("케익 정보"),
									fieldWithPath("content[].cakeInfo.cakeCategory").type(JsonFieldType.STRING).description("케익 카테고리"),
									fieldWithPath("content[].cakeInfo.cakeSize").type(JsonFieldType.STRING).description("케익 사이즈"),
									fieldWithPath("content[].cakeInfo.cakeHeight").type(JsonFieldType.STRING).description("케익 높이"),
									fieldWithPath("content[].cakeInfo.breadFlavor").type(JsonFieldType.STRING).description("빵 맛"),
									fieldWithPath("content[].cakeInfo.creamFlavor").type(JsonFieldType.STRING).description("크림 맛"),
									fieldWithPath("content[].cakeInfo.requirements").type(JsonFieldType.STRING).description("추가 내용"),
									fieldWithPath("content[].offerCount").type(JsonFieldType.NUMBER).description("업체가 제안한 건수"),
									fieldWithPath("content[].images").type(JsonFieldType.ARRAY).description("이미지 "),
									fieldWithPath("content[].createdAt").type(JsonFieldType.STRING).description("생성 시간")
							)
					));
		}
	}

	@Nested
	@DisplayName("getOrder")
	@Transactional
	class GetOrder {
		@Test
		@DisplayName("Success - 주문 상세 조회 성공하여 200으로 응답한다.")
		@Transactional
		void getOrderSuccess() throws Exception {
			Order order = orderRepository.save(getOrder(1L));

			Offer offer1 = getOffer(2L, 30000, "제안1");
			Offer offer2 = getOffer(2L, 50000, "제안2");
			offer1.setOrder(order);
			offer2.setOrder(order);

			offerRepository.saveAll(
					List.of(
							offer1,
							offer2
					));

			String imageUrl1 = "testUrl1";
			String imageUrl2 = "testUrl2";
			imageRepository.save(new Image(order.getId(), ORDER, imageUrl1));
			imageRepository.save(new Image(order.getId(), ORDER, imageUrl2));

			mockMvc.perform(get("/api/v1/orders/{orderId}", order.getId()))
					.andExpect(status().isOk())
					.andExpect(jsonPath("memberId").value(1))
					.andExpect(jsonPath("title").value(order.getTitle()))
					.andExpect(jsonPath("region").value(order.getRegion()))
					.andExpect(jsonPath("orderStatus").value(order.getOrderStatus().toString()))
					.andExpect(jsonPath("hopePrice").value(order.getHopePrice()))
					.andExpect(jsonPath("visitDate").value(
							order.getVisitDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
					.andExpect(jsonPath("cakeInfo.cakeCategory").value(order.getCakeInfo().getCakeCategory().toString()))
					.andExpect(jsonPath("cakeInfo.cakeSize").value(order.getCakeInfo().getCakeSize().toString()))
					.andExpect(jsonPath("cakeInfo.cakeHeight").value(order.getCakeInfo().getCakeHeight().toString()))
					.andExpect(jsonPath("cakeInfo.breadFlavor").value(order.getCakeInfo().getBreadFlavor().toString()))
					.andExpect(jsonPath("cakeInfo.creamFlavor").value(order.getCakeInfo().getCreamFlavor().toString()))
					.andExpect(jsonPath("cakeInfo.requirements").value(order.getCakeInfo().getRequirements()))
					.andExpect(jsonPath("offerCount").value(order.getOffers().size()))
					.andExpect(jsonPath("images[0]").value(imageUrl1))
					.andExpect(jsonPath("images[1]").value(imageUrl2))
					.andDo(print())
					.andDo(document("order/주문 상세 조회 성공",
							getDocumentRequest(),
							getDocumentResponse(),
							responseFields(
									fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("주문 아이디"),
									fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("작성자 아이디"),
									fieldWithPath("title").type(JsonFieldType.STRING).description("주문 제목"),
									fieldWithPath("region").type(JsonFieldType.STRING).description("희망 지역"),
									fieldWithPath("orderStatus").type(JsonFieldType.STRING).description("주문 상태"),
									fieldWithPath("hopePrice").type(JsonFieldType.NUMBER).description("희망 가격"),
									fieldWithPath("visitDate").type(JsonFieldType.STRING).description("희망 방문 시간"),
									fieldWithPath("cakeInfo").type(JsonFieldType.OBJECT).description("케익 정보"),
									fieldWithPath("cakeInfo.cakeCategory").type(JsonFieldType.STRING).description("케익 카테고리"),
									fieldWithPath("cakeInfo.cakeSize").type(JsonFieldType.STRING).description("케익 사이즈"),
									fieldWithPath("cakeInfo.cakeHeight").type(JsonFieldType.STRING).description("케익 높이"),
									fieldWithPath("cakeInfo.breadFlavor").type(JsonFieldType.STRING).description("빵 맛"),
									fieldWithPath("cakeInfo.creamFlavor").type(JsonFieldType.STRING).description("크림 맛"),
									fieldWithPath("cakeInfo.requirements").type(JsonFieldType.STRING).description("추가 내용"),
									fieldWithPath("offerCount").type(JsonFieldType.NUMBER).description("업체가 제안한 건수"),
									fieldWithPath("images").type(JsonFieldType.ARRAY).description("이미지"),
									fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
									fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 시간")
							)
					));
		}

		@Test
		@DisplayName("Fail - 존재하지 않는 주문에 대해 404로 응답한다.")
		@Transactional
		void getOrderFailByNotFound() throws Exception {
			int orderId = -1;
			mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
					.andExpect(status().isNotFound())
					.andDo(print())
					.andExpect(jsonPath("message").value("존재하지 않는 데이터입니다."))
					.andExpect(jsonPath("path").value("/api/v1/orders/" + orderId))
					.andExpect(jsonPath("time").exists())
					.andExpect(jsonPath("inputErrors").isEmpty())
					.andDo(document("order/주문 조회 실패 - 존재하지 않는 주문인 경우",
							getDocumentRequest(),
							getDocumentResponse(),
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("오류 메시지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("오류 경로"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("오류 발생한 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("오류가 발생한 필드")
							)
					));
		}
	}

	@Nested
	@DisplayName("createOrder")
	@Transactional
	class CreateOrder {
		@DisplayName("Success - 주문 생성 성공하여 201으로 응답한다.")
		@MethodSource("com.programmers.heycake.domain.order.argument.TestArguments#OrderCreateRequestSuccessArguments")
		@Transactional
		@ParameterizedTest
		void createOrderSuccess(
				Integer hopePrice, String region, String title,
				LocalDateTime visitTime, CakeCategory cakeCategory, CakeSize cakeSize,
				CakeHeight cakeHeight, BreadFlavor breadFlavor, CreamFlavor creamFlavor,
				String requirements, List<MultipartFile> cakeImages
		) throws Exception {

			TestUtils.setContext(1L, USER);

			MvcResult mvcResult =
					mockMvc.perform(multipart("/api/v1/orders")
									.file("cakeImages", cakeImages.get(0).getBytes())
									.file("cakeImages", cakeImages.get(1).getBytes())
									.header("access_token", "validAccessToken")
									.param("title", title)
									.param("cakeCategory", cakeCategory.toString())
									.param("cakeHeight", cakeHeight.toString())
									.param("cakeSize", cakeSize.toString())
									.param("creamFlavor", creamFlavor.toString())
									.param("breadFlavor", breadFlavor.toString())
									.param("requirements", requirements)
									.param("hopePrice", hopePrice.toString())
									.param("region", region)
									.param("visitTime", visitTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
							)
							.andExpect(status().isCreated())
							.andDo(print())
							.andDo(document("order/주문 생성 성공",
									getDocumentRequest(),
									getDocumentResponse(),
									requestParts(
											partWithName("cakeImages").description("케익 이미지")
									),
									requestHeaders(
											headerWithName("access_token").description("jwt 인증 토큰")
									),
									requestParameters(
											parameterWithName("title").description("제목"),
											parameterWithName("cakeCategory").description("케익 카테고리"),
											parameterWithName("cakeHeight").description("케익 높이"),
											parameterWithName("cakeSize").description("케익 사이즈"),
											parameterWithName("creamFlavor").description("크림 맛"),
											parameterWithName("breadFlavor").description("빵 맛"),
											parameterWithName("requirements").description("추가 요구사항"),
											parameterWithName("hopePrice").description("희망 가격"),
											parameterWithName("region").description("지역"),
											parameterWithName("visitTime").description("방문 시간")
									),
									responseHeaders(
											headerWithName("Location").description("생성된 데이터 URI")
									)
							))
							.andReturn();

			String orderId = mvcResult.getResponse()
					.getHeader("Location")
					.substring(15);

			List<Image> imageList = imageRepository.findAllByReferenceIdAndImageType(
					Long.parseLong(orderId), ORDER);

			assertThat(imageList.size()).isEqualTo(2);
		}

		@Test
		@DisplayName("Fail - 주문 생성 권한 인증에 실패하여 401으로 응답한다.")
		void createOrderFailByUnAuthorized() throws Exception {
			MockMultipartFile testImageFile = new MockMultipartFile(
					"cakeImages",
					"test.png",
					IMAGE_PNG_VALUE,
					"imageFile".getBytes()
			);

			mockMvc.perform(multipart("/api/v1/orders")
							.file("cakeImages", testImageFile.getBytes())
							.header("access_token", "inValidAccessToken")
							.param("title", "제목")
							.param("cakeCategory", "PHOTO")
							.param("cakeHeight", "ONE_LAYER")
							.param("cakeSize", "MINI")
							.param("creamFlavor", "CREAM_CHEESE")
							.param("breadFlavor", "GREEN_TEA")
							.param("requirements", "좋게 해주세요")
							.param("hopePrice", "1000000")
							.param("region", "강남구")
							.param("visitTime", "2023-02-04 11:11:11")
					)
					.andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document("order/주문 생성 실패 - 사용자 인증 실패",
							getDocumentRequest(),
							getDocumentResponse(),
							requestParts(
									partWithName("cakeImages").description("케익 이미지")
							),
							requestHeaders(
									headerWithName("access_token").description("jwt 인증 토큰")
							),
							requestParameters(
									parameterWithName("title").description("제목"),
									parameterWithName("cakeCategory").description("케익 카테고리"),
									parameterWithName("cakeHeight").description("케익 높이"),
									parameterWithName("cakeSize").description("케익 사이즈"),
									parameterWithName("creamFlavor").description("크림 맛"),
									parameterWithName("breadFlavor").description("빵 맛"),
									parameterWithName("requirements").description("추가 요구사항"),
									parameterWithName("hopePrice").description("희망 가격"),
									parameterWithName("region").description("지역"),
									parameterWithName("visitTime").description("방문 시간")
							),
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("오류 메시지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("오류가 발생한 경로"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("오류가 발생한 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("오류를 발생시킨 필드")
							)
					));
		}

		@Test
		@Transactional
		@DisplayName("Fail - 주문 생성 권한 인가에 실패하여 403로 응답한다.")
		void createOrderFailByForbidden() throws Exception {
			MockMultipartFile testImageFile = new MockMultipartFile(
					"testImageFile",
					"test.png",
					IMAGE_PNG_VALUE,
					"imageFile".getBytes()
			);

			TestUtils.setContext(1L, MARKET);

			mockMvc.perform(multipart("/api/v1/orders")
							.file("cakeImages", testImageFile.getBytes())
							.header("access_token", "inValidRoleToken")
							.param("title", "제목")
							.param("cakeCategory", "PHOTO")
							.param("cakeHeight", "ONE_LAYER")
							.param("cakeSize", "MINI")
							.param("creamFlavor", "CREAM_CHEESE")
							.param("breadFlavor", "GREEN_TEA")
							.param("requirements", "좋게 해주세요")
							.param("hopePrice", "1000000")
							.param("region", "강남구")
							.param("visitTime", "2023-02-04 11:11:11")
					)
					.andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document("order/주문 생성 실패 - 권한 인가 실패",
							getDocumentRequest(),
							getDocumentResponse(),
							requestParts(
									partWithName("cakeImages").description("케익 이미지")
							),
							requestHeaders(
									headerWithName("access_token").description("jwt 인증 토큰")
							),
							requestParameters(
									parameterWithName("title").description("제목"),
									parameterWithName("cakeCategory").description("케익 카테고리"),
									parameterWithName("cakeHeight").description("케익 높이"),
									parameterWithName("cakeSize").description("케익 사이즈"),
									parameterWithName("creamFlavor").description("크림 맛"),
									parameterWithName("breadFlavor").description("빵 맛"),
									parameterWithName("requirements").description("추가 요구사항"),
									parameterWithName("hopePrice").description("희망 가격"),
									parameterWithName("region").description("지역"),
									parameterWithName("visitTime").description("방문 시간")
							),
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("오류 메시지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("오류가 발생한 경로"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("오류가 발생한 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("오류를 발생시킨 필드")
							)
					));
		}

		@DisplayName("Fail - 잘못된 입력값으로 주문 생성에 실패하여 400으로 응답한다.")
		@MethodSource("com.programmers.heycake.domain.order.argument.TestArguments#OrderCreateRequestFailArguments")
		@ParameterizedTest
		void createOrderFailByInvalidArgument(
				Integer hopePrice, String region, String title,
				LocalDateTime visitTime, CakeCategory cakeCategory, CakeSize cakeSize,
				CakeHeight cakeHeight, BreadFlavor breadFlavor, CreamFlavor creamFlavor,
				String requirements, List<MultipartFile> cakeImages
		) throws Exception {
			Long memberId = 1L;
			SecurityContext securityContext = SecurityContextHolder.getContext();
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
					new UsernamePasswordAuthenticationToken(
							memberId,
							null,
							List.of(new SimpleGrantedAuthority(USER.getRole()))
					);
			securityContext.setAuthentication(usernamePasswordAuthenticationToken);

			mockMvc.perform(multipart("/api/v1/orders")
							.file("cakeImages", cakeImages != null ? cakeImages.get(0).getBytes() : null)
							.file("cakeImages", cakeImages != null ? cakeImages.get(1).getBytes() : null)
							.header("access_token", "validAccessToken")
							.param("title", title)
							.param("cakeCategory", cakeCategory != null ? cakeCategory.toString() : null)
							.param("cakeHeight", cakeHeight != null ? cakeHeight.toString() : null)
							.param("cakeSize", cakeSize != null ? cakeSize.toString() : null)
							.param("creamFlavor", creamFlavor != null ? creamFlavor.toString() : null)
							.param("breadFlavor", breadFlavor != null ? breadFlavor.toString() : null)
							.param("requirements", requirements)
							.param("hopePrice", hopePrice != null ? hopePrice.toString() : null)
							.param("region", region)
							.param("visitTime", visitTime != null
									? visitTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")) : null
							)
					)
					.andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document("order/주문 생성 실패 - 잘못된 입력값이 들어온 경우",
							getDocumentRequest(),
							getDocumentResponse(),
							requestParts(
									partWithName("cakeImages").description("케익 이미지")
							),
							requestHeaders(
									headerWithName("access_token").description("jwt 인증 토큰")
							),
							requestParameters(
									parameterWithName("title").description("제목"),
									parameterWithName("cakeCategory").description("케익 카테고리"),
									parameterWithName("cakeHeight").description("케익 높이"),
									parameterWithName("cakeSize").description("케익 사이즈"),
									parameterWithName("creamFlavor").description("크림 맛"),
									parameterWithName("breadFlavor").description("빵 맛"),
									parameterWithName("requirements").description("추가 요구사항"),
									parameterWithName("hopePrice").description("희망 가격"),
									parameterWithName("region").description("지역"),
									parameterWithName("visitTime").description("방문 시간")
							),
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("오류 메시지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("오류가 발생한 경로"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("오류가 발생한 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.ARRAY).description("오류를 발생시킨 필드"),
									fieldWithPath("inputErrors[].field").type(JsonFieldType.STRING).description("오류를 발생시킨 필드"),
									fieldWithPath("inputErrors[].rejectedValue").type(JsonFieldType.NULL).description("오류를 발생시킨 필드"),
									fieldWithPath("inputErrors[].message").type(JsonFieldType.STRING).description("오류를 발생시킨 필드에 대한 설명")
							)
					));
		}
	}

	@Nested
	@DisplayName("deleteOrder")
	@Transactional
	class DeleteOrder {
		@Test
		@DisplayName("Success - Order 를 삭제한다.")
		void deleteOrderSuccess() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), USER);

			Order order = orderRepository.save(getOrder(member.getId()));

			//when //then
			mockMvc.perform(delete("/api/v1/orders/{orderId}", order.getId())
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isNoContent())
					.andDo(print())
					.andDo(document(
							"order/주문 삭제 성공",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("orderId").description("주문 식별자")
							)
					));
		}

		@Test
		@DisplayName("Fail - Order 삭제 실패.(BadRequest)")
		void deleteOrderBadRequest() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), USER);

			//when //then
			mockMvc.perform(delete("/api/v1/orders/{orderId}", -1)
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document(
							"order/주문 삭제 실패(BadRequest)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("orderId").description("주문 식별자")
							)
					));
		}

		@Test
		@DisplayName("Fail - Order 삭제 실패.(Unauthorized)")
		void deleteOrderUnauthorized() throws Exception {
			//given

			//when //then
			mockMvc.perform(delete("/api/v1/orders/{orderId}", 1)
							.header("access_token", INVALID_ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"order/주문 삭제 실패(Unauthorized)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("orderId").description("주문 식별자")
							)
					));
		}

		@Test
		@DisplayName("Fail - Order 삭제 실패.(Forbidden)")
		void deleteOrderForbidden() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			Member anotherMember = memberRepository.save(getMember("marketMember"));
			setContext(anotherMember.getId(), USER);

			Order order = orderRepository.save(getOrder(member.getId()));

			//when //then
			mockMvc.perform(delete("/api/v1/orders/{orderId}", order.getId())
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document(
							"order/주문 삭제 실패(Forbidden)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("orderId").description("주문 식별자")
							)
					));
		}

		@Test
		@DisplayName("Fail - Order 삭제 실패.(Conflict)")
		void deleteOrderConflict() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), USER);

			Order order = orderRepository.save(getOrder(member.getId()));
			order.upDateOrderStatus(OrderStatus.RESERVED);

			//when //then
			mockMvc.perform(delete("/api/v1/orders/{orderId}", order.getId())
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isConflict())
					.andDo(print())
					.andDo(document(
							"order/주문 삭제 실패(Conflict)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("orderId").description("주문 식별자")
							)
					));
		}
	}
}
