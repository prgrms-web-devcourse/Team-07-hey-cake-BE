package com.programmers.heycake.domain.member.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.facade.MemberFacade;
import com.programmers.heycake.domain.member.model.dto.request.AuthenticationCodeRequest;
import com.programmers.heycake.domain.member.model.dto.request.TokenRefreshRequest;
import com.programmers.heycake.domain.member.model.dto.response.TokenResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberFacade memberFacade;

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(
			@RequestBody AuthenticationCodeRequest authenticationCodeRequest) {
		TokenResponse tokenResponse = memberFacade.loginForKakao(authenticationCodeRequest.code());
		return ResponseEntity.ok(tokenResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		memberFacade.logout();
		return ResponseEntity.noContent().build();
	}

	@PostMapping("api/v1/members/refresh")
	public ResponseEntity<String> refreshToken(
			@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest
	) {
		TokenResponse tokenResponse = memberFacade.reissueToken(tokenRefreshRequest.refreshToken());
		return ResponseEntity.ok(tokenResponse.refreshToken());
	}
}
