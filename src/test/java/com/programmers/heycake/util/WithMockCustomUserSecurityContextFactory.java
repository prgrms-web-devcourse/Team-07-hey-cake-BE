package com.programmers.heycake.util;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import com.programmers.heycake.domain.member.model.vo.MemberAuthority;

@Component
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

	public void createSecurityContext(Long memberId, MemberAuthority[] roles) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		Authentication auth =
				UsernamePasswordAuthenticationToken.authenticated(memberId, "password",
						Arrays.stream(roles)
								.map(it -> new SimpleGrantedAuthority(it.getRole()))
								.collect(Collectors.toList()));
		context.setAuthentication(auth);
	}
}
