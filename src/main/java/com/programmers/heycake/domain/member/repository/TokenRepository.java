package com.programmers.heycake.domain.member.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.programmers.heycake.domain.member.model.entity.Token;

public interface TokenRepository extends CrudRepository<Token, String> {

	Optional<Token> findByMemberId(Long memberId);
}