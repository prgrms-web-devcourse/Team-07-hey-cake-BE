package com.programmers.heycake;

import com.programmers.heycake.domain.member.model.Member;
import com.programmers.heycake.domain.member.model.Role;
import com.programmers.heycake.domain.member.model.Token;
import com.programmers.heycake.domain.member.model.dto.MemberDto;
import com.programmers.heycake.domain.member.model.dto.response.MemberResponse;

public class TestUtils {

	public static MemberResponse createMemberResponse() {
		return new MemberResponse(
				1L,
				"email@naver.com",
				"0925",
				"profileImageUrl",
				"nickname",
				Role.USER
		);
	}

	public static MemberDto createMemberDto() {
		return new MemberDto(
				"email@naver.com",
				"0925",
				"profileImageUrl",
				"nickname");
	}

	public static Member createMember() {
		return Member.builder()
				.nickname("nickname")
				.email("email@naver.com")
				.role(Role.USER)
				.birth("0925")
				.imageUrl("member profile image url")
				.build();
	}

	public static Token createToken() {
		return new Token("email@naver.com", "refreshToken");
	}
}