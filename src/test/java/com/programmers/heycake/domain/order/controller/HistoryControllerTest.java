package com.programmers.heycake.domain.order.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.heycake.domain.order.facade.HistoryFacade;
import com.programmers.heycake.domain.order.model.vo.request.HistoryControllerRequest;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class HistoryControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	HistoryFacade historyFacade;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void createOrder() {
		//order 생성
	}

	@BeforeEach
	void createOffer() {
		//offer 생성
	}

	@Nested
	@DisplayName("createHistory")
	class CreateHistory {
		@Test
		@WithMockUser
		@DisplayName("Success - orderHistory 를 생성한다. - createHistory")
		void createHistorySuccess() throws Exception {
			//given
			HistoryControllerRequest historyControllerRequest = new HistoryControllerRequest(1L, 1L);

			//when //then
			mockMvc.perform(post("/api/v1/histories")
							//TODO 헤더추가
							// 		.headers("access_token", "asdfad")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(historyControllerRequest))
							.with(csrf())
					).andExpect(status().isCreated())
					.andDo(print())
					.andDo(document(
							"history/주문 확정 생성",
							requestHeaders(
									//	TODO 멤버 헤더
									// headerWithName("access_token").description("access token")
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

}
