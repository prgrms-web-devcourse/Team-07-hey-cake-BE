package com.programmers.heycake.domain.member.controller;

import static com.programmers.heycake.common.util.ApiDocumentUtils.*;
import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.heycake.common.jwt.Jwt;
import com.programmers.heycake.domain.member.model.dto.request.AuthenticationCodeRequest;
import com.programmers.heycake.domain.member.model.dto.request.TokenRefreshRequest;
import com.programmers.heycake.domain.member.model.dto.response.TokenResponse;
import com.programmers.heycake.domain.member.model.entity.Token;
import com.programmers.heycake.domain.member.repository.TokenRepository;

@SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class MemberControllerTest {

	private static final String GET_ACCESS_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
	private static final String GET_MEMBER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	RestTemplate restTemplate;

	MockRestServiceServer mockRestServiceServer;

	@Autowired
	Jwt jwt;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	InMemoryClientRegistrationRepository inMemoryClientRegistrationRepository;

	@AfterEach
	void tearDown() {
		tokenRepository.deleteAll();
	}

	@Nested
	@DisplayName("login")
	@Transactional
	class Login {
		@Test
		@DisplayName("Success - 로그인에 성공하여 200으로 응답한다.")
		void loginSuccess() throws Exception {
			ClientRegistration kakaoRegistration = inMemoryClientRegistrationRepository.findByRegistrationId("kakao");
			LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();

			body.add("code", "authorizedCode");
			body.add("grant_type", "authorization_code");
			body.add("client_id", kakaoRegistration.getClientId());
			body.add("redirect_uri", kakaoRegistration.getRedirectUri());
			body.add("client_secret", kakaoRegistration.getClientSecret());

			String getAccessTokenResponseBody = """ 
					{
						"token_type":"bearer",
						"access_token":"accessToken",
						"expires_in":43199,
						"refresh_token":"refresh_token",
						"refresh_token_expires_in":25184000,
						"scope":"account_email profile"
					}""";

			MockRestServiceServer.MockRestServiceServerBuilder builder = MockRestServiceServer.bindTo(restTemplate);

			mockRestServiceServer = builder.build();
			mockRestServiceServer.expect(requestTo(GET_ACCESS_TOKEN_URL))
					.andExpect(method(HttpMethod.POST))
					.andExpect(
							MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE,
									"application/x-www-form-urlencoded;charset=utf-8")
					)
					.andExpect(MockRestRequestMatchers.content().formData(body))
					.andRespond(withSuccess(getAccessTokenResponseBody, MediaType.APPLICATION_JSON));

			String getMemberInfoResponseBody = """	
					{
					    "id":123456789,
					    "connected_at": "2022-04-11T01:45:28Z",
					    "kakao_account": {
					        "profile_nickname_needs_agreement	": false,
					        "profile_image_needs_agreement	": false,
					        "profile": {
					            "nickname": "홍길동",
					            "thumbnail_image_url": "http://yyy.kakao.com/.../img_110x110.jpg",
					            "profile_image_url": "http://yyy.kakao.com/dn/.../img_640x640.jpg",
					            "is_default_image":false
					        },
					        "name_needs_agreement":false,
					        "name":"홍길동",
					        "email_needs_agreement":false,
					        "is_email_valid": true,
					        "is_email_verified": true,
					        "email": "sample@sample.com",
					        "age_range_needs_agreement":false,
					        "age_range":"20~29",
					        "birthyear_needs_agreement": false,
					        "birthyear": "2002",
					        "birthday_needs_agreement": true,
					        "birthday":"1130",
					        "birthday_type":"SOLAR",
					        "gender_needs_agreement":false,
					        "gender":"female",
					        "phone_number_needs_agreement": false,
					        "phone_number": "+82 010-1234-5678",
					        "ci_needs_agreement": false,
					        "ci": "${CI}",
					        "ci_authenticated_at": "2019-03-11T11:25:22Z",
					    },
					    "properties":{
					        "${CUSTOM_PROPERTY_KEY}": "${CUSTOM_PROPERTY_VALUE}"
					    },
					    "for_partner": {
					        "uuid": "${UUID}"
					    }
					}
					""";

			mockRestServiceServer
					.expect(requestTo(GET_MEMBER_INFO_URL))
					.andExpect(method(HttpMethod.POST))
					.andExpect(
							MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE,
									"application/x-www-form-urlencoded;charset=utf-8"))
					.andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
					.andRespond(withSuccess(getMemberInfoResponseBody, MediaType.APPLICATION_JSON));

			AuthenticationCodeRequest authenticationCodeRequest = new AuthenticationCodeRequest("authorizedCode");

			mockMvc.perform(post("/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(authenticationCodeRequest))
					)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("accessToken").exists())
					.andExpect(MockMvcResultMatchers.jsonPath("refreshToken").exists())
					.andDo(document("member/oauth 로그인 성공",
							getDocumentRequest(),
							getDocumentResponse(),
							requestFields(
									fieldWithPath("code").type(JsonFieldType.STRING).description("인가 코드")
							),
							responseFields(
									fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
									fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
							)
					));
			mockRestServiceServer.verify();
		}

		@Test
		@DisplayName("Fail - 인가 코드가 잘못된 경우 400으로 응답한다.")
		void loginFailByUnAuthorized() throws Exception {

			ClientRegistration kakaoRegistration = inMemoryClientRegistrationRepository.findByRegistrationId("kakao");
			LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
			body.add("code", "invalid_authorizedCode");
			body.add("grant_type", "authorization_code");
			body.add("client_id", kakaoRegistration.getClientId());
			body.add("redirect_uri", kakaoRegistration.getRedirectUri());
			body.add("client_secret", kakaoRegistration.getClientSecret());

			String getAccessTokenResponseBody = """ 
					{
						400 Bad Request:\\"{\\"error\\":\\"invalid_grant\\",\\"error_description\\":\\"authorization code not found
						for code=invalid_authorizedCode\\",\\"error_code\\":\\"KOE320\\"}\\
					}""";

			MockRestServiceServer.MockRestServiceServerBuilder builder = MockRestServiceServer.bindTo(restTemplate);
			mockRestServiceServer = builder.build();

			mockRestServiceServer.expect(requestTo(GET_ACCESS_TOKEN_URL))
					.andExpect(method(HttpMethod.POST))
					.andExpect(
							MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE,
									"application/x-www-form-urlencoded;charset=utf-8")
					)
					.andExpect(MockRestRequestMatchers.content().formData(body))
					.andRespond(withSuccess(getAccessTokenResponseBody, MediaType.APPLICATION_JSON));

			AuthenticationCodeRequest authenticationCodeRequest = new AuthenticationCodeRequest("invalid_authorizedCode");

			mockMvc.perform(post("/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(authenticationCodeRequest))
					)
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andDo(document("member/oauth 로그인 실패 - 인가 코드가 잘못 되었을 경우",
							getDocumentRequest(),
							getDocumentResponse(),
							requestFields(
									fieldWithPath("code").type(JsonFieldType.STRING).description("인가 코드")
							),
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("오류 메시지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("오류 경로"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("오류 발생한 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("오류가 발생한 필드")
							)
					));
			mockRestServiceServer.verify();
		}
	}

	@Nested
	@DisplayName("logout")
	@Transactional
	class Logout {
		@Test
		@DisplayName("Success - 로그아웃에 성공하여 200으로 응답한다")
		void logoutSuccess() throws Exception {

			TokenResponse tokenResponse = jwt.generateAllToken(
					Jwt.Claims.from(
							1L, new String[] {USER.getRole()}
					));
			tokenRepository.save(new Token(tokenResponse.refreshToken(), tokenResponse.accessToken(), 1L));

			mockMvc.perform(post("/logout")
							.contentType(MediaType.APPLICATION_JSON)
							.header("access_token", tokenResponse.accessToken())
					)
					.andDo(print())
					.andExpect(status().isNoContent())
					.andDo(document("member/oauth 로그아웃 성공",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("jwt 인증 토큰")
							)
					));

			Assertions.assertThat(tokenRepository.findByMemberId(1L).isEmpty()).isTrue();
		}

		@Test
		@DisplayName("Fail 사용자 인증이 실패하면, 401 응답으로 실패한다")
		void logoutFailByUnAuthorized() throws Exception {
			mockMvc.perform(post("/logout")
							.contentType(MediaType.APPLICATION_JSON)
					)
					.andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document("member/oauth 로그아웃 실패 - 인증에 실패한 경우",
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
	@DisplayName("refreshToken")
	@Transactional
	class refreshToken {
		@Test
		@DisplayName("Success - 토큰 재발급에 성공하여 200으로 응답한다")
		void refreshTokenSuccess() throws Exception {
			TokenResponse tokenResponse = jwt.generateAllToken(
					Jwt.Claims.from(
							1L, new String[] {USER.getRole()}
					));

			tokenRepository.save(new Token(tokenResponse.refreshToken(), tokenResponse.accessToken(), 1L));

			mockMvc.perform(post("/api/v1/members/refresh")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(
									new TokenRefreshRequest(tokenResponse.accessToken(), tokenResponse.refreshToken())))
					)
					.andExpect(status().isOk())
					.andDo(print())
					.andDo(document("member/refresh token 재발급 성공",
							getDocumentRequest(),
							getDocumentResponse(),
							requestFields(
									fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
									fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
							),
							responseFields(
									fieldWithPath("accessToken").type(JsonFieldType.STRING).description("재발급 받은 액세스 토큰"),
									fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("재발급 받은 리프레시 토큰")
							)
					));
		}

		@Test
		@DisplayName("Fail - 빈 토큰으로 요청하여 400으로 응답한다")
		void refreshTokenFailByInvalidRequest() throws Exception {
			TokenResponse tokenResponse = jwt.generateAllToken(
					Jwt.Claims.from(
							1L, new String[] {USER.getRole()}
					));

			tokenRepository.save(new Token(tokenResponse.refreshToken(), tokenResponse.accessToken(), 1L));

			mockMvc.perform(post("/api/v1/members/refresh")
							.contentType(MediaType.APPLICATION_JSON)
					)
					.andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document("member/refresh token 재발급 실패 - 토큰값 없이 요청한 경우",
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

		@Test
		@DisplayName("Fail - 만료된 토큰인 경우 401으로 응답한다")
		@Transactional
		void refreshTokenFailByTokenExpired() throws Exception {
			TokenResponse tokenResponse = jwt.generateAllToken(
					Jwt.Claims.from(
							1L, new String[] {USER.getRole()}
					));

			mockMvc.perform(post("/api/v1/members/refresh")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(new TokenRefreshRequest(
									tokenResponse.accessToken(), tokenResponse.refreshToken()))
							))
					.andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document("member/refresh token 재발급 실패 - 리프레시 토큰이 만료되었을 경우",
							getDocumentRequest(),
							getDocumentResponse(),
							requestFields(
									fieldWithPath("accessToken").description("액세스 토큰"),
									fieldWithPath("refreshToken").description("리프레시 토큰")
							),
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("오류 메시지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("오류 경로"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("오류 발생한 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("오류가 발생한 필드")
							)
					));
		}
	}
}