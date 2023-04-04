package com.programmers.heycake.domain.member.controller;

import static com.programmers.heycake.common.util.ApiDocumentUtils.*;
import static com.programmers.heycake.common.util.TestUtils.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.model.vo.MemberAuthority;
import com.programmers.heycake.domain.member.repository.MemberRepository;

@Transactional
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class FollowControllerTest {
	private static final String ACCESS_TOKEN = "access_token";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MarketEnrollmentRepository marketEnrollmentRepository;

	@Autowired
	private MarketRepository marketRepository;

	@Nested
	@DisplayName("createFollow")
	@Transactional
	class CreateFollow {
		@Test
		@DisplayName("Success - follow 를 생성한다.")
		void createFollowSuccess() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.USER);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isCreated())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 생성 성공",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
							),
							responseHeaders(
									headerWithName("location").description("저장 url")
							)
					));
		}

		@Test
		@DisplayName("Fail - follow 생성 실패. (BadRequest) - not exists market id")
		void createFollowBadRequest() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.USER);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId() + 1)
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 생성 실패(BadRequest)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
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
		@DisplayName("Fail - follow 생성 실패. (Unauthorized) - Unauthorized")
		void createFollowUnauthorized() throws Exception {
			//given
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 생성 실패(Unauthorized)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
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
		@DisplayName("Fail - follow 생성 실패. (Forbidden) - role is not USER")
		void createFollowForbidden() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.MARKET);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 생성 실패(Forbidden)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
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
		@DisplayName("Fail - follow 생성 실패. (Conflict) 이미 follow된 경우")
		void createFollowConflictAlreadyFollow() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.USER);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.USER);

			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId())
					.header("access_token", ACCESS_TOKEN));

			//when // then
			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isConflict())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 생성 실패(Conflict)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
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
	@DisplayName("deleteFollow")
	@Transactional
	class DeleteFollow {
		@Test
		@DisplayName("Success - follow 를 삭제한다.")
		void deleteFollowSuccess() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.USER);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId())
					.header("access_token", ACCESS_TOKEN));

			//when // then
			mockMvc.perform(delete("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isNoContent())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 삭제 성공",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
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
		@DisplayName("Fail - follow 를 삭제 실패.(BadRequest) 존재하지 않는 market_id")
		void deleteFollowBadRequestNotExistsMarketId() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.USER);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId())
					.header("access_token", ACCESS_TOKEN));

			//when // then
			mockMvc.perform(delete("/api/v1/follows/{marketId}", market.getId() + 1)
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 삭제 실패(BadRequest)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
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
		@DisplayName("Fail - follow 를 삭제 실패.(BadRequest) 팔로우 하지 않은 market")
		void deleteFollowBadRequestNotFollowed() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.USER);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(delete("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 삭제 실패(BadRequest)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
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
		@DisplayName("Fail - follow 를 삭제 실패.(Unauthorized) Unauthorized")
		void deleteFollowUnauthorized() throws Exception {
			//given
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(delete("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 삭제 실패(Unauthorized)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
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
		@DisplayName("Fail - follow 를 삭제 실패.(Forbidden)")
		void deleteFollowForbidden() throws Exception {
			//given
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(memberForMarket.getId(), MemberAuthority.MARKET);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(delete("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 삭제 실패(Forbidden)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
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

}
