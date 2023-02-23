package com.programmers.heycake.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.*;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class CustomOauth2UserServiceTest {
	@InjectMocks
	private CustomOauth2UserService customOauth2UserService;

	@Mock
	private DefaultOAuth2UserService defaultOAuth2UserService;

	@Nested
	@DisplayName("loadUser")
	class LoadUser {

		@Test
		@DisplayName("Fail - userRequest가 비어있다면 Exception을 던진다. - loadUser")
		void loadUserUserRequestFail() {
			//given
			OAuth2UserRequest userRequest = null;

			//when //then
			assertThrows(IllegalArgumentException.class, () -> customOauth2UserService.loadUser(userRequest));
		}

		// @Test
		// @DisplayName("Success - OAuth2UserRequest를 OAuth2User로 변환한다. - loadUser")
		// void loadUserToMapSuccess() {
		// 	//given
		// 	ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("kakao")
		// 			// .authorizationGrantType(null)
		// 			// .build();
		// 			.authorizationGrantType(AUTHORIZATION_CODE)
		// 			.clientId("clientId")
		// 			.redirectUri("redirectUri")
		// 			.authorizationUri("authorizationUri")
		// 			.tokenUri("tokenUri")
		// 			.build();
		// 	OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(BEARER, "tokenValue", null, null);
		// 	// OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(null, null, null, null);
		//
		// 	Map<String, Object> profile = new HashMap<>();
		// 	profile.put("nickname", "nickname");
		// 	profile.put("profile_image_url", "profileImage");
		// 	Map<String, Object> account = new HashMap<>();
		// 	account.put("email", "email");
		// 	account.put("birthday", "birthday");
		// 	Map<String, Object> attributes = new HashMap<>();
		// 	attributes.put("profile", profile);
		// 	attributes.put("kakao_account", account);
		// 	attributes.put("key", "key");
		//
		// 	OAuth2User oAuth2User = new OAuth2User() {
		// 		@Override
		// 		public Map<String, Object> getAttributes() {
		// 			return attributes;
		// 		}
		//
		// 		@Override
		// 		public Collection<? extends GrantedAuthority> getAuthorities() {
		// 			return null;
		// 		}
		//
		// 		@Override
		// 		public String getName() {
		// 			return null;
		// 		}
		// 	};
		//
		// 	OAuth2UserRequest oAuth2UserRequest = new OAuth2UserRequest(clientRegistration, oAuth2AccessToken);
		//
		// 	//when
		// 	// when(oAuth2UserService.loadUser(any()))
		// 	// 		.thenReturn(oAuth2User);
		// 	when(defaultOAuth2UserService.loadUser(any()))
		// 			.thenReturn(oAuth2User);
		//
		// 	//then
		// 	Map<String, Object> resultAttributes = customOauth2UserService.loadUser(oAuth2UserRequest).getAttributes();
		// 	assertThat(resultAttributes)
		// 			.contains(
		// 					entry("profile", profile),
		// 					entry("kakao_account", account),
		// 					entry("key", "key")
		// 			);
		// }

		// 	@Test
		// 	@DisplayName("Success - OAuth2UserRequest를 OAuth2User로 변환한다. - loadUser")
		// 	void loadUserToMapSuccess() {
		// 		//given
		// 		ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("kakao").build();
		// 		OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(null, null, null, null);
		// 		Map<String, Object> profile = new HashMap<>();
		// 		profile.put("nickname", "nickname");
		// 		profile.put("profile_image_url", "profileImage");
		// 		Map<String, Object> account = new HashMap<>();
		// 		account.put("email", "email");
		// 		account.put("birthday", "birthday");
		// 		Map<String, Object> attributes = new HashMap<>();
		// 		attributes.put("profile", profile);
		// 		attributes.put("kakao_account", account);
		// 		attributes.put("key", "key");
		//
		// 		OAuth2User oAuth2User = new OAuth2User() {
		// 			@Override
		// 			public Map<String, Object> getAttributes() {
		// 				return attributes;
		// 			}
		//
		// 			@Override
		// 			public Collection<? extends GrantedAuthority> getAuthorities() {
		// 				return null;
		// 			}
		//
		// 			@Override
		// 			public String getName() {
		// 				return null;
		// 			}
		// 		};
		//
		// 		OAuth2UserRequest oAuth2UserRequest = new OAuth2UserRequest(clientRegistration, oAuth2AccessToken);
		//
		// 		//when
		// 		when(defaultOAuth2UserService.loadUser(any()))
		// 				.thenReturn(oAuth2User);
		//
		// 		//then
		// 		Map<String, Object> resultAttributes = customOauth2UserService.loadUser(oAuth2UserRequest).getAttributes();
		// 		assertThat(resultAttributes)
		// 				.contains(
		// 						entry("profile", profile),
		// 						entry("kakao_account", account),
		// 						entry("key", "key")
		// 				);
		// 	}
	}
}
