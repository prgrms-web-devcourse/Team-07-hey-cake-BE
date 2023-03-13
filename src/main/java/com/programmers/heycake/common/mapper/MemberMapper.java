package com.programmers.heycake.common.mapper;

import com.programmers.heycake.domain.member.model.dto.response.MemberResponse;
import com.programmers.heycake.domain.member.model.entity.Member;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberMapper {

	public static MemberResponse toMemberResponse(Member member) {
		return MemberResponse.builder()
				.memberId(member.getId())
				.email(member.getEmail())
				.memberAuthority(member.getMemberAuthority())
				.birth(member.getBirth())
				.nickname(member.getNickname())
				.build();
	}
}
