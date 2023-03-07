package com.programmers.heycake.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import com.programmers.heycake.domain.member.model.vo.MemberAuthority;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
	long memberId() default 2;

	MemberAuthority[] roles() default {MemberAuthority.USER};
}
