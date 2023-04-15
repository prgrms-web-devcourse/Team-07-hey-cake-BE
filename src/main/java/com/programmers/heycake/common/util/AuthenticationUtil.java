package com.programmers.heycake.common.util;

import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationUtil {
	public static Long getMemberId() {
		if (isAnonymous()) {
			throw new AuthenticationException("인증 실패") {/*no ops*/
			};
		}
		return (Long)getAuthentication().getPrincipal();
	}

	public static boolean isAnonymous() {
		Authentication authentication = getAuthentication();
		return authentication == null || authentication.getPrincipal().equals("anonymousUser");
	}

	public static boolean isValidAccess(Long memberId) {
		return Objects.equals(AuthenticationUtil.getMemberId(), memberId);
	}

	private static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
}
