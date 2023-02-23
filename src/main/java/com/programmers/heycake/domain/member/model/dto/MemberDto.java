package com.programmers.heycake.domain.member.model.dto;

public record MemberDto(
		String email,
		String birthday,
		String imageUrl,
		String nickname
) {
}
