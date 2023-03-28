package com.programmers.heycake.domain.member.service;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;

import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.jwt.Jwt;
import com.programmers.heycake.domain.member.model.dto.response.TokenResponse;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.model.entity.Token;
import com.programmers.heycake.domain.member.repository.TokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
	private final Jwt jwt;
	private final TokenRepository tokenRepository;

	@Transactional
	public TokenResponse publishToken(Member member) {
		Optional<Token> foundToken = tokenRepository.findByMemberId(member.getId());

		TokenResponse tokenResponse = jwt.generateAllToken(
				Jwt.Claims.from(
						member.getId(),
						new String[] {
								member.getMemberAuthority().getRole()
						})
		);

		Token newToken = new Token(
				member.getId(),
				tokenResponse.refreshToken()
		);

		if (foundToken.isPresent()) {
			foundToken.get().updateRefreshToken(tokenResponse.refreshToken());
		} else {
			tokenRepository.save(newToken);
		}
		return tokenResponse;
	}

	@Transactional
	public void deleteToken() {
		tokenRepository.findByMemberId(getMemberId())
				.ifPresent(tokenRepository::delete);
	}

	@Transactional
	public TokenResponse reissueToken(String refreshToken) {
		Optional<Token> optionalToken = tokenRepository.findTokenByRefreshToken(refreshToken);
		if (optionalToken.isEmpty()) {
			throw new AccessDeniedException("token 발급 제한");
		}
		Long memberId;
		String[] roles;

		try {
			Jwt.Claims claims = jwt.verify(optionalToken.get().getRefreshToken());
			memberId = claims.getMemberId();
			roles = claims.getRoles();
		} catch (Exception e) {
			log.warn("Jwt 처리중 문제가 발생하였습니다 : {}", e.getMessage());
			throw e;
		} finally {
			tokenRepository.delete(optionalToken.get());
			tokenRepository.flush();
		}

		TokenResponse tokenResponse = jwt.generateAllToken(
				Jwt.Claims
						.from(memberId, roles)
		);
		Token token = new Token(memberId, tokenResponse.refreshToken());
		tokenRepository.save(token);

		return tokenResponse;
	}
}
