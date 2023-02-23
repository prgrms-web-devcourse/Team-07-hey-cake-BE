package com.programmers.heycake.domain.member.service;

import static com.programmers.heycake.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.programmers.heycake.domain.member.model.Token;
import com.programmers.heycake.domain.member.model.dto.MemberDto;
import com.programmers.heycake.domain.member.model.dto.response.MemberResponse;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.member.repository.TokenRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	private MemberService memberService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private TokenRepository tokenRepository;

	@Nested
	@DisplayName("isMember")
	class IsMember {

		@Test
		@DisplayName("Success - 등록된 이메일이 있다면 true를 반환한다. - isMember")
		void isMemberReturnTrueSuccess() {
			// given
			String email = "email@naver.com";
			when(memberRepository.existsByEmail(email))
					.thenReturn(true);

			// when
			boolean isMember = memberService.isMember(email);

			// then
			verify(memberRepository).existsByEmail(email);
			assertThat(isMember).isTrue();
		}

		@Test
		@DisplayName("Success - 등록된 이메일이 없다면 false를 반환한다. - isMember")
		void isMemberReturnFalseSuccess() {
			// given
			String email = "email@naver.com";
			when(memberRepository.existsByEmail(email))
					.thenReturn(false);

			// when
			boolean isMember = memberService.isMember(email);

			// then
			verify(memberRepository).existsByEmail(email);
			assertThat(isMember).isFalse();
		}
	}

	@Nested
	@DisplayName("findByEmail")
	class FindByEmail {

		@Test
		@DisplayName("Success - 등록된 이메일로 회원을 반환한다. - findByEmail")
		void findByEmailSuccess() {
			// given
			MemberResponse memberResponse = createMemberResponse();
			String email = memberResponse.email();
			Member member = Member.builder()
					.email(memberResponse.email())
					.nickname(memberResponse.nickname())
					.birth(memberResponse.birth())
					.memberAuthority(memberResponse.memberAuthority())
					.imageUrl(memberResponse.imageUrl())
					.build();

			when(memberRepository.findByEmail(email))
					.thenReturn(Optional.of(member));

			// when
			MemberResponse findMemberResponse = memberService.findByEmail(email);

			// then
			verify(memberRepository).findByEmail(email);
			assertThat(findMemberResponse)
					.usingRecursiveComparison()
					.ignoringFields("id")
					.isEqualTo(memberResponse);
		}

		@Test
		@DisplayName("Fail - 없는 이메일로 조회하여 EntityNotFoundException을 던진다. - findByEmail")
		void findByEmailFail() {
			// given
			MemberResponse memberResponse = createMemberResponse();
			String email = memberResponse.email();

			when(memberRepository.findByEmail(email))
					.thenReturn(Optional.empty());

			// when, then
			assertThrows(EntityNotFoundException.class, () -> memberService.findByEmail(email));
			verify(memberRepository).findByEmail(email);
		}
	}

	@Nested
	@DisplayName("signUp")
	class SignUp {
		@Test
		@DisplayName("Success - 회원가입에 성공한다. - signUp")
		void signUpSuccess() {
			// given
			MemberDto memberDto = createMemberDto();
			Member member = createMember();

			when(memberRepository.saveAndFlush(any(Member.class)))
					.thenReturn(member);

			// when
			memberService.signUp(memberDto);

			// then
			verify(memberRepository).saveAndFlush(any(Member.class));
		}
	}

	@Nested
	@DisplayName("saveToken")
	class SaveToken {
		@Test
		@DisplayName("Success - 토큰 저장에 성공한다. - saveToken")
		void saveTokenSuccess() {
			// given
			Token token = createToken();

			when(tokenRepository.saveAndFlush(any(Token.class)))
					.thenReturn(token);

			// when
			memberService.saveToken(token);

			// then
			verify(tokenRepository).saveAndFlush(any(Token.class));
		}
	}

}
