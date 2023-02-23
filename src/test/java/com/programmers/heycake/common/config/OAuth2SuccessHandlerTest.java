package com.programmers.heycake.common.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.TestUtils;
import com.programmers.heycake.domain.member.model.Token;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.member.repository.TokenRepository;

@Transactional
@SpringBootTest
class OAuth2SuccessHandlerTest {

	@Autowired
	private OAuth2SuccessHandler oAuth2SuccessHandler;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private TokenRepository tokenRepository;

	HttpServletRequest request = mock(HttpServletRequest.class);
	HttpServletResponse response = mock(HttpServletResponse.class);
	PrintWriter printWriter = mock(PrintWriter.class);
	Authentication authentication;
	Member member = TestUtils.createMember();

	@BeforeEach
	void setUp() {
		// Principal
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("email", member.getEmail());
		attributes.put("birthday", member.getBirth());
		attributes.put("profileImage", member.getImageUrl());
		attributes.put("nickname", member.getNickname());
		attributes.put("key", "key");

		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
		OAuth2User oAuth2User = new DefaultOAuth2User(authorities, attributes, "key");

		// Aauthentication
		authentication = new UsernamePasswordAuthenticationToken(oAuth2User, null, authorities);

		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authentication);
	}

	@Nested
	@DisplayName("onAuthenticationSuccess")
	@Transactional
	class OnAuthenticationSuccess {

		@Test
		@DisplayName("Success - 이미 존재하는 회원인 경우 회원가입(signup) 없이 토큰 저장 - onAuthenticationSuccess")
		void onAuthenticationSuccessWhenExistsMember() throws Exception {
			// given
			memberRepository.save(member);

			when(response.getWriter())
					.thenReturn(printWriter);

			// when
			oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

			// then
			List<Token> tokens = tokenRepository.findAll();
			assertThat(tokens).hasSize(1);
			assertThat(tokens.get(0).getEmail()).isEqualTo(member.getEmail());
		}

		@Test
		@DisplayName("Success - 존재하지 않는 회원인 경우 회원가입(signup) 후 토큰 저장 - onAuthenticationSuccess")
		void onAuthenticationSuccessWhenNotExistsMember() throws Exception {
			// given
			when(response.getWriter())
					.thenReturn(printWriter);

			// when
			oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

			// then
			List<Member> members = memberRepository.findAll();
			List<Token> tokens = tokenRepository.findAll();

			assertThat(members).hasSize(1);
			assertThat(members.get(0))
					.usingRecursiveComparison()
					.ignoringFields("id")
					.isEqualTo(member);
			assertThat(tokens).hasSize(1);
			assertThat(tokens.get(0).getEmail()).isEqualTo(member.getEmail());
		}
	}

}
