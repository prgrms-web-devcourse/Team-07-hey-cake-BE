package com.programmers.heycake.domain.facade;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.member.model.dto.request.TokenRefreshRequest;
import com.programmers.heycake.domain.member.model.dto.response.TokenResponse;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.model.entity.Token;
import com.programmers.heycake.domain.member.service.MemberService;
import com.programmers.heycake.domain.member.service.TokenService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberFacade {

	private final MemberService memberService;

	private final TokenService tokenService;

	@Transactional
	public TokenResponse loginForKakao(String authorizedCode) {
		Member member = memberService.loginForKakao(authorizedCode);
		return tokenService.publishToken(member);
	}

	@Transactional
	public void logout() {
		tokenService.deleteToken();
	}

	@Transactional
	public Token reissueToken(TokenRefreshRequest tokenRefreshRequest) {
		return tokenService.reissueToken(tokenRefreshRequest);
	}
}
