package com.programmers.heycake.common.jwt;

import static java.util.Collections.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {

	private final Jwt jwt;
	private final String accessToken;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;

		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			String token = getAccessToken(httpServletRequest);
			if (token != null) {
				try {
					Jwt.Claims claims = verify(token);
					Long memberId = claims.memberId;
					List<GrantedAuthority> authorities = getAuthorities(claims);

					if (memberId != null && authorities.size() > 0) {
						UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
								new UsernamePasswordAuthenticationToken(memberId, null, authorities);
						SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
					}
				} catch (Exception e) {
					log.warn("Jwt 처리중 문제가 발생하였습니다 : {}", e.getMessage());
				}
			}
		} else {
			log.debug("이미 인증 객체가 존재합니다 : {}",
					SecurityContextHolder.getContext().getAuthentication());
		}
		chain.doFilter(httpServletRequest, httpServletResponse);
	}

	private String getAccessToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		String token = null;
		if (cookies != null) {
			token = Arrays
					.stream(cookies)
					.filter(cookie -> cookie.getName().equals(accessToken))
					.map(Cookie::getValue)
					.findFirst()
					.orElse("");
		}
		if (token != null && !token.isBlank()) {
			try {
				return URLDecoder.decode(token, StandardCharsets.UTF_8);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
			}
		}
		return null;
	}

	private Jwt.Claims verify(String token) {
		return jwt.verify(token);
	}

	private List<GrantedAuthority> getAuthorities(Jwt.Claims claims) {
		String[] roles = claims.roles;
		if (roles == null || roles.length == 0) {
			return emptyList();
		}
		return Arrays.stream(roles)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}
}