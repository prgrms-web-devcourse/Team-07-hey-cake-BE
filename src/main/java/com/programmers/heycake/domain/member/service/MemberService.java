package com.programmers.heycake.domain.member.service;

import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.*;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.member.model.dto.MemberInfo;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private static final String GET_ACCESS_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
	private static final String GET_MEMBER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

	private final RestTemplate restTemplate;

	private final MemberRepository memberRepository;

	private final InMemoryClientRegistrationRepository inMemoryClientRegistrationRepository;

	@Transactional
	public Member loginForKakao(String authorizedCode) {

		String accessToken = getAccessToken(authorizedCode);
		MemberInfo memberInfo = getMemberInfo(accessToken);

		if (!memberRepository.existsByEmail(memberInfo.email())) {
			memberRepository.save(
					new Member(memberInfo.email(), USER, memberInfo.birthday(), memberInfo.nickname())
			);
		}
		return memberRepository.findByEmail(memberInfo.email()).get();
	}

	@Transactional(readOnly = true)
	public Member getMemberById(Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public boolean isMarketById(Long memberId) {
		return getMemberById(memberId).isMarket();
	}

	private String getAccessToken(String authorizedCode) {
		HttpHeaders headers = getAccessTokenRequestHeader();

		ClientRegistration kakaoRegistration = inMemoryClientRegistrationRepository.findByRegistrationId("kakao");
		MultiValueMap<String, String> body = getAccessTokenRequestBody(authorizedCode, kakaoRegistration);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				GET_ACCESS_TOKEN_URL,
				HttpMethod.POST,
				request,
				String.class
		);

		JSONObject responseBody = new JSONObject(response.getBody());

		return responseBody.getString("access_token");
	}

	private HttpHeaders getAccessTokenRequestHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");
		return headers;
	}

	private MultiValueMap<String, String> getAccessTokenRequestBody(String authorizedCode,
			ClientRegistration kakaoRegistration) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

		body.add("code", authorizedCode);
		body.add("grant_type", "authorization_code");
		body.add("client_id", kakaoRegistration.getClientId());
		body.add("redirect_uri", kakaoRegistration.getRedirectUri());
		body.add("client_secret", kakaoRegistration.getClientSecret());
		return body;
	}

	private MemberInfo getMemberInfo(String accessToken) {
		HttpHeaders headers = getMemberInfoRequestHeader(accessToken);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
				GET_MEMBER_INFO_URL,
				HttpMethod.POST,
				request,
				String.class
		);

		return createMemberInfoFromKakaoResponse(response);
	}

	private HttpHeaders getMemberInfoRequestHeader(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		return headers;
	}

	private MemberInfo createMemberInfoFromKakaoResponse(ResponseEntity<String> response) {
		JSONObject responseBody = new JSONObject(response.getBody());

		String email = responseBody.getJSONObject("kakao_account")
				.getString("email");

		boolean hasBirthday = responseBody.getJSONObject("kakao_account")
				.has("birthday");

		String birthday = null;
		if (hasBirthday) {
			birthday = responseBody.getJSONObject("kakao_account")
					.getString("birthday");
		}

		String profileUrl = responseBody.getJSONObject("kakao_account")
				.getJSONObject("profile")
				.getString("profile_image_url");

		String nickname = responseBody.getJSONObject("kakao_account")
				.getJSONObject("profile")
				.getString("nickname");

		return new MemberInfo(email, birthday, profileUrl, nickname);
	}
}
