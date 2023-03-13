package com.programmers.heycake.domain.member.model.dto.response;

import com.programmers.heycake.domain.member.model.vo.MemberAuthority;

import lombok.Builder;

@Builder
public record MemberResponse(Long memberId, String email, MemberAuthority memberAuthority, String birth,
														 String nickname) {
}
