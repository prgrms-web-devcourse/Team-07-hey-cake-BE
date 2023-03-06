package com.programmers.heycake.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.programmers.heycake.domain.member.model.vo.MemberAuthority;

public class CustomUserDetails implements UserDetails {

	long memberId;

	private List<GrantedAuthority> authorities;

	public CustomUserDetails(long memberId, MemberAuthority[] roles) {
		this.memberId = memberId;
		this.authorities = Arrays.stream(roles)
				.map(it -> new SimpleGrantedAuthority(it.getRole()))
				.collect(Collectors.toList());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

}
