package com.programmers.heycake.domain.order.controller;

import static com.programmers.heycake.util.TestUtils.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.model.vo.MemberAuthority;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.order.repository.OrderRepository;

@Transactional
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class OrderControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	MemberRepository memberRepository;

	private static final String ACCESS_TOKEN = "access_token";

	void setOrders(Member member, int amount) {
		for (int i = 0; i < amount; i++) {
			orderRepository.save(getOrder(member.getId()));
		}
	}

	@Nested
	@DisplayName("getOrderList")
	@Transactional
	class GetOrderList {
		@Test
		@DisplayName("Success - getOrderList 조회한다.")
		void getOrderListSuccess() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), MemberAuthority.USER);
			setOrders(member, 10);

			//when //then
			mockMvc.perform(get("/api/v1/orders/my?cursorId=1&pageSize=10&orderStatus=NEW")
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isOk())
					.andDo(print())
					.andDo(document(
							"order/주문 목록 조회",
							requestHeaders(
									headerWithName("access_token").description("access token")
							),
							requestParameters(
									parameterWithName("cursorId").description("cursor order id"),
									parameterWithName("pageSize").description("order amount"),
									parameterWithName("orderStatus").description("search option - order status")
							),
							responseFields(
									fieldWithPath("myOrderResponseList").description("order list"),
									fieldWithPath("myOrderResponseList[].id").description("order id"),
									fieldWithPath("myOrderResponseList[].title").description("order title"),
									fieldWithPath("myOrderResponseList[].orderStatus").description("order orderStatus"),
									fieldWithPath("myOrderResponseList[].region").description("order region"),
									fieldWithPath("myOrderResponseList[].visitTime").description("order visitTime"),
									fieldWithPath("myOrderResponseList[].createdAt").description("order createdAt"),
									fieldWithPath("myOrderResponseList[].imageUrl").description("order imageUrl"),
									fieldWithPath("lastCursorDate").description("lastCursorDate")
							)
					));
		}

		@Test
		@DisplayName("Fail - getOrderList 조회 실패.(BadRequest)")
		void getOrderListBadRequest() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));

			setContext(member.getId(), MemberAuthority.USER);
			setOrders(member, 10);

			//when //then
			mockMvc.perform(get("/api/v1/orders/my?cursorId=1&pageSize=10&orderStatus=ADSDF")
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document(
							"order/주문 목록 조회",
							requestHeaders(
									headerWithName("access_token").description("access token")
							),
							requestParameters(
									parameterWithName("cursorId").description("cursor order id"),
									parameterWithName("pageSize").description("order amount"),
									parameterWithName("orderStatus").description("search option - order status")
							),
							responseFields(
									fieldWithPath("message").description("error message"),
									fieldWithPath("path").description("url path"),
									fieldWithPath("time").description("error time"),
									fieldWithPath("inputErrors").description("error details"),
									fieldWithPath("inputErrors[].field").description("invalid field"),
									fieldWithPath("inputErrors[].rejectedValue").description("input value"),
									fieldWithPath("inputErrors[].message").description("detail message")

							)
					));
		}

		@Test
		@DisplayName("Fail - getOrderList 조회 실패.(Unauthorized)")
		void getOrderListUnauthorized() throws Exception {
			//given

			//when //then
			mockMvc.perform(get("/api/v1/orders/my?cursorId=1&pageSize=10&orderStatus=NEW")
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"order/주문 목록 조회",
							requestHeaders(
									headerWithName("access_token").description("access token")
							),
							requestParameters(
									parameterWithName("cursorId").description("cursor order id"),
									parameterWithName("pageSize").description("order amount"),
									parameterWithName("orderStatus").description("search option - order status")
							),
							responseFields(
									fieldWithPath("message").description("error message"),
									fieldWithPath("path").description("url path"),
									fieldWithPath("time").description("error time"),
									fieldWithPath("inputErrors").description("error details")
							)
					));
		}

		@Test
		@WithAnonymousUser
		@DisplayName("Fail - getOrderList 조회 실패.(Forbidden)")
		void getOrderListForbidden() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), MemberAuthority.ADMIN);

			//when //then
			mockMvc.perform(get("/api/v1/orders/my?cursorId=1&pageSize=10&orderStatus=NEW")
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document(
							"order/주문 목록 조회",
							requestHeaders(
									headerWithName("access_token").description("access token")
							),
							requestParameters(
									parameterWithName("cursorId").description("cursor order id"),
									parameterWithName("pageSize").description("order amount"),
									parameterWithName("orderStatus").description("search option - order status")
							),
							responseFields(
									fieldWithPath("message").description("error message"),
									fieldWithPath("path").description("url path"),
									fieldWithPath("time").description("error time"),
									fieldWithPath("inputErrors").description("error details")
							)
					));
		}

	}

}
