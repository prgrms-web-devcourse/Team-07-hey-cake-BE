package com.programmers.heycake.domain.member.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.member.model.TokenResponse;
import com.programmers.heycake.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/refresh")
	public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody String refreshToken) {
		return ResponseEntity.ok(memberService.reissueToken(refreshToken));
	}
}

