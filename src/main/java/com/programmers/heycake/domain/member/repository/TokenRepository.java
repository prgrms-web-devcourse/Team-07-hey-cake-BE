package com.programmers.heycake.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.member.model.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

	Optional<Token> findByMemberId(Long memberId);

	Optional<Token> findTokenByRefreshToken(String refreshToken);
}