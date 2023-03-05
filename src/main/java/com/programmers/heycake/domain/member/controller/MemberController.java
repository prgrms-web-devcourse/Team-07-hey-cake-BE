package com.programmers.heycake.domain.member.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.member.model.dto.request.TokenRefreshRequest;
import com.programmers.heycake.domain.member.model.dto.response.TokenResponse;
import com.programmers.heycake.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

	private static final String LOGIN_DONE_REDIRECT_URL = "http://localhost:3000/main";

	private final MemberService memberService;

	@GetMapping("/login")
	public void login(
			@RequestParam String code, HttpServletResponse response
	) throws IOException {
		log.info("login start: {}", code);

		TokenResponse tokenResponse = memberService.loginForKakao(code);

		response.setHeader("access_token", tokenResponse.accessToken());
		response.setHeader("refresh_token", tokenResponse.refreshToken());
		response.sendRedirect(LOGIN_DONE_REDIRECT_URL);

		log.info("login end: {}", code);
	}

	@PostMapping("api/v1/members/refresh")
	public ResponseEntity<String> refreshToken(
			@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest,
			HttpServletResponse response
	) {
		TokenResponse tokenResponse = memberService.reissueToken(tokenRefreshRequest.refreshToken());
		Cookie accessToken = new Cookie("access_token", tokenResponse.accessToken());
		accessToken.setPath("/");
		accessToken.setHttpOnly(true);
		response.addCookie(accessToken);
		return ResponseEntity.ok(tokenResponse.refreshToken());
	}
}
