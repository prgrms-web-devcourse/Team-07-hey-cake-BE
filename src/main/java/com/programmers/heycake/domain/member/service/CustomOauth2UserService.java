package com.programmers.heycake.domain.member.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.programmers.heycake.domain.member.model.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		Assert.notNull(userRequest, "userRequest cannot be null");

		OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

		System.out.println(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		String userNameAttributeName = userRequest.getClientRegistration()
				.getProviderDetails()
				.getUserInfoEndpoint()
				.getUserNameAttributeName();

		Map<String, Object> memberAttribute = toMap(registrationId, userNameAttributeName, oAuth2User.getAttributes());

		return new DefaultOAuth2User(
				Collections.singleton(new SimpleGrantedAuthority(Role.USER.getAuthority())),
				memberAttribute,
				"email"
		);

	}

	private Map<String, Object> toMap(
			String provider,
			String attributeKey,
			Map<String, Object> attributes
	) {
		//이거 생략해도 될거같음
		if (!provider.equals("kakao")) {
			throw new RuntimeException("잘못된 요청.");
		}

		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

		Map<String, Object> map = new HashMap<>();
		map.put("key", attributeKey);
		map.put("profileImage", kakaoProfile.get("profile_image_url"));
		map.put("email", kakaoAccount.get("email"));
		map.put("nickname", kakaoProfile.get("nickname"));
		map.put("birthday", kakaoAccount.get("birthday"));

		return map;
	}

}
