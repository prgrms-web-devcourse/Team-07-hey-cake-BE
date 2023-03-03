package com.programmers.heycake.domain.member.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.member.model.dto.request.AuthorizationCodeRequest;
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

	@PostMapping("/login/oauth2/code/kakao")
	public ResponseEntity<String> getAuthorizationCode(
			@RequestBody AuthorizationCodeRequest authorizationCodeRequest, HttpServletResponse response
	) {
		TokenResponse tokenResponse = memberService.loginForKakao(authorizationCodeRequest.code());
		Cookie accessToken = new Cookie("access_token", tokenResponse.token());
		accessToken.setPath("/");
		accessToken.setHttpOnly(true);
		response.addCookie(accessToken);
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
