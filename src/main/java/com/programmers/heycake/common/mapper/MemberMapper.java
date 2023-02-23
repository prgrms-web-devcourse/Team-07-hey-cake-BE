package com.programmers.heycake.common.mapper;

import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;

import com.programmers.heycake.domain.member.model.dto.MemberDto;
import com.programmers.heycake.domain.member.model.dto.response.MemberResponse;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.model.vo.MemberAuthority;

public class MemberMapper {

	public static MemberDto toMemberDto(OAuth2User oAuth2User) {
		Map<String, Object> attributes = oAuth2User.getAttributes();
		return new MemberDto(
				(String)attributes.get("email"),
				(String)attributes.get("birthday"),
				(String)attributes.get("profileImage"),
				(String)attributes.get("nickname")
		);
	}

	public static Member toMember(MemberDto memberDto) {
		return Member
				.builder()
				.email(memberDto.email())
				.birth(memberDto.birthday())
				.memberAuthority(MemberAuthority.USER)
				.nickname(memberDto.nickname())
				.imageUrl(memberDto.imageUrl())
				.build();
	}

	public static MemberResponse toMemberResponse(Member member) {
		return new MemberResponse(
				member.getId(),
				member.getEmail(),
				member.getBirth(),
				member.getImageUrl(),
				member.getNickname(),
				member.getMemberAuthority()
		);
	}

}
