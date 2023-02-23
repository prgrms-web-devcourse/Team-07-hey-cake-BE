package com.programmers.heycake.domain.member.model.dto.response;

import com.programmers.heycake.domain.member.model.vo.MemberAuthority;

public record MemberResponse(
		Long id,
		String email,
		String birth,
		String imageUrl,
		String nickname,
		MemberAuthority memberAuthority
) {
}
