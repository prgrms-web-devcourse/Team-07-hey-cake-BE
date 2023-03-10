package com.programmers.heycake.domain.member.model.dto;

public record MemberInfo(
		String email,
		String birthday,
		String profileUrl,
		String nickname
) {
}
