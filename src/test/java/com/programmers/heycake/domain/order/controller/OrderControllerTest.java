package com.programmers.heycake.domain.order.controller;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;
import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.MARKET;
import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.*;
import static com.programmers.heycake.util.TestUtils.*;
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

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.service.ImageService;
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
import com.programmers.heycake.util.TestUtils;

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
	private ImageService imageService;

	private static final String ACCESS_TOKEN = "access_token";

	void setOrders(Member member, int amount) {
		for (int i = 0; i < amount; i++) {
			orderRepository.save(getOrder(member.getId()));
		}
	}

	@Nested
	@DisplayName("getMyOrderList")
	@Transactional
	class GetMyOrderList {
		@Test
		@DisplayName("Success - getMyOrderList ????????????.")
		void getMyOrderListSuccess() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), USER);
			setOrders(member, 10);

			//when //then
			mockMvc.perform(get("/api/v1/orders/my?cursorId=1&pageSize=10&orderStatus=NEW")
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isOk())
					.andDo(print())
					.andDo(document(
							"orders/?????? ?????? ?????? ??????",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							requestParameters(
									parameterWithName("cursorId").description("?????? ?????? ?????????"),
									parameterWithName("pageSize").description("????????? ??????"),
									parameterWithName("orderStatus").description("?????? ??????")
							),
							responseFields(
									fieldWithPath("myOrderResponseList").description("?????? ??????"),
									fieldWithPath("myOrderResponseList[].id").description("?????? ?????????"),
									fieldWithPath("myOrderResponseList[].title").description("?????? ??????"),
									fieldWithPath("myOrderResponseList[].orderStatus").description("?????? ??????"),
									fieldWithPath("myOrderResponseList[].region").description("?????? ??????"),
									fieldWithPath("myOrderResponseList[].visitTime").description("?????? ??????"),
									fieldWithPath("myOrderResponseList[].createdAt").description("?????? ??????"),
									fieldWithPath("myOrderResponseList[].cakeInfo").description("?????? ??????"),
									fieldWithPath("myOrderResponseList[].cakeInfo.cakeCategory").description("?????? ??????"),
									fieldWithPath("myOrderResponseList[].cakeInfo.cakeSize").description("?????? ??????"),
									fieldWithPath("myOrderResponseList[].cakeInfo.cakeHeight").description("?????? ??????"),
									fieldWithPath("myOrderResponseList[].cakeInfo.breadFlavor").description("??? ???"),
									fieldWithPath("myOrderResponseList[].cakeInfo.creamFlavor").description("?????? ???"),
									fieldWithPath("myOrderResponseList[].cakeInfo.requirements").description("?????? ??????"),
									fieldWithPath("myOrderResponseList[].hopePrice").description("?????? ??????"),
									fieldWithPath("myOrderResponseList[].imageUrl").description("????????? ??????"),
									fieldWithPath("cursorId").description("?????? ?????????")
							)));
		}

		@Test
		@DisplayName("Fail - getMyOrderList ?????? ??????.(BadRequest)")
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
							"orders/?????? ?????? ?????? ??????(BadRequest)",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							requestParameters(
									parameterWithName("cursorId").description("?????? ?????? ?????????"),
									parameterWithName("pageSize").description("????????? ??????"),
									parameterWithName("orderStatus").description("?????? ??????")
							),
							responseFields(
									fieldWithPath("message").description("?????? ?????????"),
									fieldWithPath("path").description("?????? ?????? uri"),
									fieldWithPath("time").description("?????? ?????? ??????"),
									fieldWithPath("inputErrors").description("?????? ??????"),
									fieldWithPath("inputErrors[].field").description("?????? ??????"),
									fieldWithPath("inputErrors[].rejectedValue").description("?????? ???"),
									fieldWithPath("inputErrors[].message").description("?????? ?????????")
							)
					));
		}

		@Test
		@DisplayName("Fail - getMyOrderList ?????? ??????.(Unauthorized)")
		void getMyOrderListUnauthorized() throws Exception {
			//given

			//when //then
			mockMvc.perform(get("/api/v1/orders/my?cursorId=1&pageSize=10&orderStatus=NEW")
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"orders/?????? ?????? ?????? ??????(Unauthorized)",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							requestParameters(
									parameterWithName("cursorId").description("?????? ?????? ?????????"),
									parameterWithName("pageSize").description("????????? ??????"),
									parameterWithName("orderStatus").description("?????? ??????")
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
		@WithAnonymousUser
		@DisplayName("Fail - getMyOrderList ?????? ??????.(Forbidden)")
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
							"orders/?????? ?????? ?????? ??????(Forbidden)",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							requestParameters(
									parameterWithName("cursorId").description("?????? ?????? ?????????"),
									parameterWithName("pageSize").description("????????? ??????"),
									parameterWithName("orderStatus").description("?????? ??????")
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

	@Nested
	@DisplayName("?????? ?????? ??????")
	@Transactional
	class GetOrderTest {
		@Test
		@DisplayName("Success - ?????? ?????? ?????? ??????")
		@Transactional
		void getOrderSuccess() throws Exception {
			Order order = orderRepository.save(getOrder(1L));

			Offer offer1 = getOffer(2L, 30000, "??????1");
			Offer offer2 = getOffer(2L, 50000, "??????2");
			offer1.setOrder(order);
			offer2.setOrder(order);

			offerRepository.saveAll(
					List.of(
							offer1,
							offer2
					));

			String imageUrl1 = "testUrl1";
			String imageUrl2 = "testUrl2";
			imageService.createImage(order.getId(), ORDER, imageUrl1);
			imageService.createImage(order.getId(), ORDER, imageUrl2);

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
					.andDo(document("orders/?????? ?????? ??????",
							responseFields(
									fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
									fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("????????? ?????????"),
									fieldWithPath("title").type(JsonFieldType.STRING).description("?????? ??????"),
									fieldWithPath("region").type(JsonFieldType.STRING).description("?????? ??????"),
									fieldWithPath("orderStatus").type(JsonFieldType.STRING).description("?????? ??????"),
									fieldWithPath("hopePrice").type(JsonFieldType.NUMBER).description("?????? ??????"),
									fieldWithPath("visitDate").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
									fieldWithPath("cakeInfo").type(JsonFieldType.OBJECT).description("?????? ??????"),
									fieldWithPath("cakeInfo.cakeCategory").type(JsonFieldType.STRING).description("?????? ????????????"),
									fieldWithPath("cakeInfo.cakeSize").type(JsonFieldType.STRING).description("?????? ?????????"),
									fieldWithPath("cakeInfo.cakeHeight").type(JsonFieldType.STRING).description("?????? ??????"),
									fieldWithPath("cakeInfo.breadFlavor").type(JsonFieldType.STRING).description("??? ???"),
									fieldWithPath("cakeInfo.creamFlavor").type(JsonFieldType.STRING).description("?????? ???"),
									fieldWithPath("cakeInfo.requirements").type(JsonFieldType.STRING).description("?????? ??????"),
									fieldWithPath("offerCount").type(JsonFieldType.NUMBER).description("????????? ????????? ??????"),
									fieldWithPath("images").type(JsonFieldType.ARRAY).description("?????????"),
									fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????? ??????"),
									fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("?????? ??????")
							)
					));
		}

		@Test
		@DisplayName("Fail - ?????? ?????? ?????? ??????.(NotFound)")
		@Transactional
		void getOrderFailByNotFound() throws Exception {
			int orderId = -1;
			mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
					.andExpect(status().isNotFound())
					.andDo(print())
					.andExpect(jsonPath("message").value("???????????? ?????? ??????????????????."))
					.andExpect(jsonPath("path").value("/api/v1/orders/" + orderId))
					.andExpect(jsonPath("time").exists())
					.andExpect(jsonPath("inputErrors").isEmpty())
					.andDo(document("orders/?????? ?????? ??????",
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("?????? ??????"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("?????? ????????? ??????"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("????????? ????????? ??????")
							)
					));
		}
	}

	@Nested
	@Transactional
	class CreateOrderTest {
		@DisplayName("Success - Order ?????? ??????")
		@MethodSource("com.programmers.heycake.domain.order.argument.TestArguments#OrderCreateRequestSuccessArguments")
		@Transactional
		@ParameterizedTest
		void createOrderSuccess(
				Integer hopePrice, String region, String title,
				LocalDateTime visitTime, CakeCategory cakeCategory, CakeSize cakeSize,
				CakeHeight cakeHeight, BreadFlavor breadFlavor, CreamFlavor creamFlavor,
				String requirements, List<MultipartFile> cakeImages
		) throws Exception {

			Long memberId = 1L;

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
							.andDo(document("orders/?????? ?????? ??????",
									requestParts(
											partWithName("cakeImages").description("?????? ?????????")
									),
									requestHeaders(
											headerWithName("access_token").description("jwt ?????? ??????")
									),
									requestParameters(
											parameterWithName("title").description("??????"),
											parameterWithName("cakeCategory").description("?????? ????????????"),
											parameterWithName("cakeHeight").description("?????? ??????"),
											parameterWithName("cakeSize").description("?????? ?????????"),
											parameterWithName("creamFlavor").description("?????? ???"),
											parameterWithName("breadFlavor").description("??? ???"),
											parameterWithName("requirements").description("?????? ????????????"),
											parameterWithName("hopePrice").description("?????? ??????"),
											parameterWithName("region").description("??????"),
											parameterWithName("visitTime").description("?????? ??????")
									),
									responseHeaders(
											headerWithName("Location").description("????????? ????????? URI")
									)
							))
							.andReturn();

			String orderId = mvcResult.getResponse()
					.getHeader("Location")
					.substring(15);
			ImageResponses imageResponses = imageService.getImages(Long.parseLong(orderId), ORDER);
			List<ImageResponse> images = imageResponses.images();

			assertThat(images.size()).isEqualTo(2);
		}

		@Test
		@DisplayName("Fail - Order ?????? ??????.(UnAuthorized)")
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
							.param("title", "??????")
							.param("cakeCategory", "PHOTO")
							.param("cakeHeight", "ONE_LAYER")
							.param("cakeSize", "MINI")
							.param("creamFlavor", "CREAM_CHEESE")
							.param("breadFlavor", "GREEN_TEA")
							.param("requirements", "?????? ????????????")
							.param("hopePrice", "1000000")
							.param("region", "?????????")
							.param("visitTime", "2023-02-04 11:11:11")
					)
					.andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document("orders/?????? ?????? ??????(UnAuthorize)",
							requestParts(
									partWithName("cakeImages").description("?????? ?????????")
							),
							requestHeaders(
									headerWithName("access_token").description("jwt ?????? ??????")
							),
							requestParameters(
									parameterWithName("title").description("??????"),
									parameterWithName("cakeCategory").description("?????? ????????????"),
									parameterWithName("cakeHeight").description("?????? ??????"),
									parameterWithName("cakeSize").description("?????? ?????????"),
									parameterWithName("creamFlavor").description("?????? ???"),
									parameterWithName("breadFlavor").description("??? ???"),
									parameterWithName("requirements").description("?????? ????????????"),
									parameterWithName("hopePrice").description("?????? ??????"),
									parameterWithName("region").description("??????"),
									parameterWithName("visitTime").description("?????? ??????")
							),
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("????????? ???????????? ??????")
							)
					));
		}

		@Test
		@Transactional
		@DisplayName("Fail - Order ?????? ??????.(Forbidden)")
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
							.param("title", "??????")
							.param("cakeCategory", "PHOTO")
							.param("cakeHeight", "ONE_LAYER")
							.param("cakeSize", "MINI")
							.param("creamFlavor", "CREAM_CHEESE")
							.param("breadFlavor", "GREEN_TEA")
							.param("requirements", "?????? ????????????")
							.param("hopePrice", "1000000")
							.param("region", "?????????")
							.param("visitTime", "2023-02-04 11:11:11")
					)
					.andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document("orders/?????? ?????? ??????(Forbidden)",
							requestParts(
									partWithName("cakeImages").description("?????? ?????????")
							),
							requestHeaders(
									headerWithName("access_token").description("jwt ?????? ??????")
							),
							requestParameters(
									parameterWithName("title").description("??????"),
									parameterWithName("cakeCategory").description("?????? ????????????"),
									parameterWithName("cakeHeight").description("?????? ??????"),
									parameterWithName("cakeSize").description("?????? ?????????"),
									parameterWithName("creamFlavor").description("?????? ???"),
									parameterWithName("breadFlavor").description("??? ???"),
									parameterWithName("requirements").description("?????? ????????????"),
									parameterWithName("hopePrice").description("?????? ??????"),
									parameterWithName("region").description("??????"),
									parameterWithName("visitTime").description("?????? ??????")
							),
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("????????? ???????????? ??????")
							)
					));
		}

		@DisplayName("Fail - Order ?????? ??????.(invalidArgument)")
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
							.param("visitTime", visitTime != null ?
									visitTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")) : null
							)
					)
					.andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document("orders/?????? ?????? ??????(Forbidden)",
							requestParts(
									partWithName("cakeImages").description("?????? ?????????")
							),
							requestHeaders(
									headerWithName("access_token").description("jwt ?????? ??????")
							),
							requestParameters(
									parameterWithName("title").description("??????"),
									parameterWithName("cakeCategory").description("?????? ????????????"),
									parameterWithName("cakeHeight").description("?????? ??????"),
									parameterWithName("cakeSize").description("?????? ?????????"),
									parameterWithName("creamFlavor").description("?????? ???"),
									parameterWithName("breadFlavor").description("??? ???"),
									parameterWithName("requirements").description("?????? ????????????"),
									parameterWithName("hopePrice").description("?????? ??????"),
									parameterWithName("region").description("??????"),
									parameterWithName("visitTime").description("?????? ??????")
							),
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
									fieldWithPath("inputErrors").type(JsonFieldType.ARRAY).description("????????? ???????????? ??????"),
									fieldWithPath("inputErrors[].field").type(JsonFieldType.STRING).description("????????? ???????????? ??????"),
									fieldWithPath("inputErrors[].rejectedValue").type(JsonFieldType.NULL).description("????????? ???????????? ??????"),
									fieldWithPath("inputErrors[].message").type(JsonFieldType.STRING).description("????????? ???????????? ????????? ?????? ??????")
							)
					));
		}
	}

	@Nested
	@DisplayName("deleteOrder")
	@Transactional
	class DeleteOrder {
		@Test
		@DisplayName("Success - Order ??? ????????????.")
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
							"orders/?????? ?????? ??????",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							pathParameters(
									parameterWithName("orderId").description("?????? ?????????")
							)
					));
		}

		@Test
		@DisplayName("Fail - Order ?????? ??????.(BadRequest)")
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
							"orders/?????? ?????? ??????(BadRequest)",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							pathParameters(
									parameterWithName("orderId").description("?????? ?????????")
							)
					));
		}

		@Test
		@DisplayName("Fail - Order ?????? ??????.(Unauthorized)")
		void deleteOrderUnauthorized() throws Exception {
			//given

			//when //then
			mockMvc.perform(delete("/api/v1/orders/{orderId}", 1)
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"orders/?????? ?????? ??????(Unauthorized)",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							pathParameters(
									parameterWithName("orderId").description("?????? ?????????")
							)
					));
		}

		@Test
		@DisplayName("Fail - Order ?????? ??????.(Forbidden)")
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
							"orders/?????? ?????? ??????(Forbidden)",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							pathParameters(
									parameterWithName("orderId").description("?????? ?????????")
							)
					));
		}

		@Test
		@DisplayName("Fail - Order ?????? ??????.(Conflict)")
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
							"orders/?????? ?????? ??????(Conflict)",
							requestHeaders(
									headerWithName("access_token").description("?????? ??????")
							),
							pathParameters(
									parameterWithName("orderId").description("?????? ?????????")
							)
					));
		}
	}
}
