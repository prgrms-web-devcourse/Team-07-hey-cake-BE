package com.programmers.heycake.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.member.model.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
