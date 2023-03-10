package com.programmers.heycake.domain.order.controller;

import static com.programmers.heycake.util.TestUtils.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
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
							"orders/주문 목록 조회 성공",
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							requestParameters(
									parameterWithName("cursorId").description("커서 주문 식별자"),
									parameterWithName("pageSize").description("페이지 크기"),
									parameterWithName("orderStatus").description("주문 상태")
							),
							responseFields(
									fieldWithPath("myOrderResponseList").description("주문 목록"),
									fieldWithPath("myOrderResponseList[].id").description("주문 식별자"),
									fieldWithPath("myOrderResponseList[].title").description("주문 제목"),
									fieldWithPath("myOrderResponseList[].orderStatus").description("주문 상태"),
									fieldWithPath("myOrderResponseList[].region").description("주문 지역"),
									fieldWithPath("myOrderResponseList[].visitTime").description("방문 시간"),
									fieldWithPath("myOrderResponseList[].createdAt").description("생성 시간"),
									fieldWithPath("myOrderResponseList[].cakeInfo").description("생성 시간"),
									fieldWithPath("myOrderResponseList[].cakeInfo.cakeCategory").description("케익 종류"),
									fieldWithPath("myOrderResponseList[].cakeInfo.cakeSize").description("케익 크기"),
									fieldWithPath("myOrderResponseList[].cakeInfo.cakeHeight").description("케익 높이"),
									fieldWithPath("myOrderResponseList[].cakeInfo.breadFlavor").description("빵 맛"),
									fieldWithPath("myOrderResponseList[].cakeInfo.creamFlavor").description("크림 맛"),
									fieldWithPath("myOrderResponseList[].cakeInfo.requirements").description("요청 사항"),
									fieldWithPath("myOrderResponseList[].hopePrice").description("희망 가"),
									fieldWithPath("myOrderResponseList[].imageUrl").description("이미지 주소"),
									fieldWithPath("cursorId").description("커서 식별자")
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
							"orders/주문 목록 조회 실패(BadRequest)",
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
		@DisplayName("Fail - getOrderList 조회 실패.(Unauthorized)")
		void getOrderListUnauthorized() throws Exception {
			//given

			//when //then
			mockMvc.perform(get("/api/v1/orders/my?cursorId=1&pageSize=10&orderStatus=NEW")
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"orders/주문 목록 조회 실패(Unauthorized)",
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
							"orders/주문 목록 조회 실패(Forbidden)",
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
	@DisplayName("deleteOrder")
	@Transactional
	class DeleteOrder {
		@Test
		@DisplayName("Success - Order 를 삭제한다.")
		void deleteOrderSuccess() throws Exception {
			//given
			Member member = memberRepository.save(getMember("member"));
			setContext(member.getId(), MemberAuthority.USER);

			Order order = orderRepository.save(getOrder(member.getId()));

			//when //then
			mockMvc.perform(delete("/api/v1/orders/{orderId}", order.getId())
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isNoContent())
					.andDo(print())
					.andDo(document(
							"orders/주문 삭제 성공",
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
			setContext(member.getId(), MemberAuthority.USER);

			//when //then
			mockMvc.perform(delete("/api/v1/orders/{orderId}", -1)
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document(
							"orders/주문 삭제 실패(BadRequest)",
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
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"orders/주문 삭제 실패(Unauthorized)",
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
			setContext(anotherMember.getId(), MemberAuthority.USER);

			Order order = orderRepository.save(getOrder(member.getId()));

			//when //then
			mockMvc.perform(delete("/api/v1/orders/{orderId}", order.getId())
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document(
							"orders/주문 삭제 실패(Forbidden)",
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
			setContext(member.getId(), MemberAuthority.USER);

			Order order = orderRepository.save(getOrder(member.getId()));
			order.upDateOrderStatus(OrderStatus.RESERVED);

			//when //then
			mockMvc.perform(delete("/api/v1/orders/{orderId}", order.getId())
							.header("access_token", ACCESS_TOKEN)
							.with(csrf())
					).andExpect(status().isConflict())
					.andDo(print())
					.andDo(document(
							"orders/주문 삭제 실패(Conflict)",
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
