package com.programmers.heycake.domain.member.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.heycake.domain.member.model.dto.request.CodeRequest;
import com.programmers.heycake.domain.member.model.dto.request.TokenRefreshRequest;
import com.programmers.heycake.domain.member.model.vo.TokenResponse;
import com.programmers.heycake.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;

	/**
	 * 1. /login/oauth2/code/kakao
	 * 2. frontend kakao redirect -> uri
	 * 3. 브라우저에서 로그인을 해요
	 * 4. -> 인증이 되면은 서버쪽에서도 엔드포인트를 열어야함
	 * 5. 그 엔드포인트에서 인가처리를하고 하고 토큰을 만들어서 프론트엔드로 다시 리다이렉트 .
	 * @param code
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/login/oauth2/code/kakao")
	public ResponseEntity<String> getAuthorizationCode(
			@RequestBody CodeRequest codeRequest, HttpServletResponse response
	) throws IOException {
		TokenResponse tokenResponse = memberService.loginForKakao(codeRequest.code());
		Cookie accessToken = new Cookie("access_token", tokenResponse.token());
		accessToken.setPath("/");
		accessToken.setHttpOnly(true);
		response.addCookie(accessToken);

		// String redirectUri = "http://localhost:3000";
		//
		// response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		// response.setStatus(HttpServletResponse.SC_OK);
		//
		// OutputStream outputStream = response.getOutputStream();
		// ObjectMapper objectMapper = new ObjectMapper();
		// objectMapper.writeValue(outputStream, tokenResponse.refreshToken());
		//
		// outputStream.flush();
		// outputStream.close();
		//
		// response.sendRedirect(redirectUri);

		return ResponseEntity.ok(tokenResponse.refreshToken());
	}

	@PostMapping("/members/refresh")
	public ResponseEntity<String> refreshToken(
			@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest,
			HttpServletResponse response
	) {
		TokenResponse tokenResponse = memberService.reissueToken(tokenRefreshRequest.refreshToken());
		Cookie accessToken = new Cookie("access_token", tokenResponse.token());
		accessToken.setPath("/");
		accessToken.setHttpOnly(true);
		response.addCookie(accessToken);
		return ResponseEntity.ok(tokenResponse.refreshToken());
	}
}

