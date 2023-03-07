package com.programmers.heycake.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory
		implements WithSecurityContextFactory<WithMockCustomUser> {
	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		CustomUserDetails principal =
				new CustomUserDetails(customUser.memberId(), customUser.roles());

		Authentication auth =
				UsernamePasswordAuthenticationToken.authenticated(customUser.memberId(), "password",
						principal.getAuthorities());
		context.setAuthentication(auth);
		return context;
	}
}
