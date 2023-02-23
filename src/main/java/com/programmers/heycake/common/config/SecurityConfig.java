package com.programmers.heycake.common.config;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.heycake.common.jwt.Jwt;
import com.programmers.heycake.common.jwt.JwtAuthFilter;
import com.programmers.heycake.domain.member.service.CustomOauth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final CustomOauth2UserService oAuth2UserService;
	private final Jwt jwt;

	@Bean
	public JwtAuthFilter jwtAuthFilter() {
		return new JwtAuthFilter(jwt, "access_token");
	}

	@Bean
	public WebSecurityCustomizer configure() {
		return (web) -> web.ignoring().mvcMatchers(
				"/swagger/**"); //예시
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, authenticationException) -> {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			OutputStream outputStream = response.getOutputStream();
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValueAsString("errorResposne");
			outputStream.flush();
			outputStream.close();

		};
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return ((request, response, accessDeniedException) -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication != null ? authentication.getPrincipal() : null;
			// TODO merge 후 errorResponse 넣어주기
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("text/plain");
			response.getWriter().write("#ErrorResponse");
			response.getWriter().flush();
			response.getWriter().close();
		});
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.anyRequest()
				//Todo 추후에 페이지 권한 나누기
				// .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
				// .antMatchers(
				// 		"/api/v1/members/signup",
				// 		"/api/v1/members/login"
				// ).hasRole("USER")
				.permitAll()
				.and()
				.csrf().disable()
				.headers().disable()
				.formLogin().disable()
				.httpBasic().disable()
				.rememberMe().disable()
				.logout().disable()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint())
				.accessDeniedHandler(accessDeniedHandler())
				.and()
				.addFilterAfter(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
				.oauth2Login()
				//TODO 추후에 엔드포인트로 만들어주기
				// .loginPage("/login/user")
				.successHandler(oAuth2SuccessHandler)
				.userInfoEndpoint()
				.userService(oAuth2UserService);

		return http.build();
	}
}
