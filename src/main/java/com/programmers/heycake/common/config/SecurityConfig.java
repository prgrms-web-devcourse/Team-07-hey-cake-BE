package com.programmers.heycake.common.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.programmers.heycake.common.exception.CustomAccessDeniedHandler;
import com.programmers.heycake.common.exception.CustomAuthenticationEntryPoint;
import com.programmers.heycake.common.jwt.Jwt;
import com.programmers.heycake.common.jwt.JwtAuthenticationFilter;
import com.programmers.heycake.common.jwt.JwtProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtProperties jwtProperties;

	@Bean
	public Jwt jwt() {
		return new Jwt(
				jwtProperties.getClientSecret(),
				jwtProperties.getIssuer(),
				jwtProperties.getTokenExpire(),
				jwtProperties.getRefreshTokenExpire()
		);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "PUT", "PATCH", "DELETE"));
		configuration.setAllowedHeaders(Arrays.asList("Content-Type"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new CustomAccessDeniedHandler();
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new CustomAuthenticationEntryPoint();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwt(), "access_token");
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.authorizeRequests()
				.anyRequest().permitAll()
				// .antMatchers("/api/v1/login").permitAll()
				// .antMatchers(HttpMethod.POST, "/api/v1/orders").hasRole("USER")
				// .antMatchers(HttpMethod.GET, "/api/v1/orders/**").permitAll()
				// .antMatchers(HttpMethod.DELETE, "/api/v1/orders/**").hasAnyRole("ADMIN", "USER")
				// .antMatchers(HttpMethod.POST, "/api/v1/histories").hasRole("USER")
				// .antMatchers(HttpMethod.GET, "/api/v1/orders/my").hasAnyRole("USER", "MARKET")
				// .antMatchers(HttpMethod.DELETE, "/api/v1/offers/**").hasAnyRole("ADMIN", "MARKET")
				// .antMatchers(HttpMethod.POST, "/api/v1/offers").hasRole("MARKET")
				// .antMatchers(HttpMethod.POST, "/api/v1/comments").hasAnyRole("ADMIN", "MARKET", "USER")
				// .antMatchers(HttpMethod.DELETE, "/api/v1/comments/**").hasAnyRole("ADMIN", "MARKET", "USER")
				// .antMatchers(HttpMethod.GET, "/api/v1/comments").permitAll()
				// .antMatchers(HttpMethod.POST, "/api/v1/enrollments").hasRole("USER")
				// .antMatchers(HttpMethod.GET, "/api/v1/enrollments/**").hasRole("ADMIN")
				// .antMatchers(HttpMethod.PATCH, "/api/v1/enrollments/**").hasRole("ADMIN")
				// .antMatchers(HttpMethod.GET, "/api/v1/markets/**").permitAll()
				// .antMatchers(HttpMethod.PATCH, "/api/v1/markets/**").hasRole("MARKET")
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
				.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.cors()
		;
		return http.build();
	}
}