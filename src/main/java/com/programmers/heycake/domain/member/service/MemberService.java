package com.programmers.heycake.domain.member.service;

import static com.programmers.heycake.common.mapper.MemberMapper.*;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.jwt.Jwt;
import com.programmers.heycake.domain.member.model.Token;
import com.programmers.heycake.domain.member.model.TokenResponse;
import com.programmers.heycake.domain.member.model.dto.MemberDto;
import com.programmers.heycake.domain.member.model.dto.response.MemberResponse;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.member.repository.TokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final TokenRepository tokenRepository;
	private final Jwt jwt;

	public boolean isMember(String email) {
		return memberRepository.existsByEmail(email);
	}

	public MemberResponse findByEmail(String email) {
		return toMemberResponse(
				memberRepository
						.findByEmail(email)
						.orElseThrow(() -> new EntityNotFoundException("등록되지 않는 email :" + email))
		);
	}

	@Transactional
	public Long signUp(MemberDto memberDto) {
		return memberRepository.saveAndFlush(
				toMember(memberDto)
		).getId();
	}

	@Transactional
	public Long saveToken(Token token) {
		return tokenRepository
				.saveAndFlush(token)
				.getId();
	}

	@Transactional
	public TokenResponse reissueToken(String refreshToken) {
		Optional<Token> optionalToken = tokenRepository.findTokenByRefreshToken(refreshToken);
		if (optionalToken.isEmpty()) {
			return null;
		}
		String email;
		String[] roles;

		try {
			Jwt.Claims claims = jwt.verify(optionalToken.get().getRefreshToken());
			email = claims.getEmail();
			roles = claims.getRoles();
		} catch (Exception e) {
			log.warn("Jwt 처리중 문제가 발생하였습니다 : {}", e.getMessage());
			throw new RuntimeException("인증 에러 뱉으세요!");
		} finally {
			tokenRepository.delete(optionalToken.get());
		}

		TokenResponse tokenResponse = jwt.generateAllToken(
				Jwt.Claims
						.from(email, roles)
		);
		Token token = new Token(email, tokenResponse.refreshToken());
		tokenRepository.save(token);

		return tokenResponse;
	}
}
