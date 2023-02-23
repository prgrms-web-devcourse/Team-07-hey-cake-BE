package com.programmers.heycake.common.config;

import static com.programmers.heycake.common.mapper.MemberMapper.*;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.heycake.common.jwt.Jwt;
import com.programmers.heycake.domain.member.model.Token;
import com.programmers.heycake.domain.member.model.TokenResponse;
import com.programmers.heycake.domain.member.model.dto.MemberDto;
import com.programmers.heycake.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final Jwt jwt;
	private final MemberService memberService;
	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication
	) throws IOException {
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		MemberDto memberDto = toMemberDto(oAuth2User);

		if (!memberService.isMember(memberDto.email())) {
			memberService.signUp(memberDto);
		}

		TokenResponse tokenResponse = jwt.generateAllToken(
				Jwt
						.Claims.from(memberDto.email(), new String[] {
								memberService.findByEmail(memberDto.email()).memberAuthority().getRole()
						})
		);
		writeTokenResponse(response, tokenResponse);
		memberService.saveToken(
				new Token(
						memberDto.email(),
						tokenResponse.refreshToken()
				)
		);
	}

	private void writeTokenResponse(HttpServletResponse response, TokenResponse tokenResponse) throws IOException {
		response.setContentType("application/json; charset=UTF-8");

		Cookie accessToken = new Cookie("access_token", tokenResponse.token());
		accessToken.setPath("/");
		accessToken.setHttpOnly(true);
		response.addCookie(accessToken);

		response.getWriter().write(objectMapper.writeValueAsString(tokenResponse.refreshToken()));
	}
}
