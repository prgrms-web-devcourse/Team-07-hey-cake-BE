package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.market.model.dto.EnrollmentRequest;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

	@Mock
	private MarketEnrollmentRepository marketEnrollmentRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private MarketEnrollment savedEnrollment;

	@InjectMocks
	private EnrollmentService enrollmentService;

	private MockMultipartFile businessLicenseImg = new MockMultipartFile(
			"businessLicense",
			"businessLicense",
			".jpg",
			"businessLicense".getBytes()
	);
	private MockMultipartFile marketImg = new MockMultipartFile(
			"market",
			"market",
			".jpg",
			"market".getBytes()
	);
	private EnrollmentRequest request = EnrollmentRequest.builder()
			.memberId(1L)
			.businessNumber("1234567890")
			.ownerName("권성준")
			.openDate(LocalDate.of(1997, 10, 10))
			.marketName("성준이네")
			.phoneNumber("01012345678")
			.city("서울특별시")
			.district("강남구")
			.detailAddress("테헤란로")
			.openTime(LocalTime.of(9, 0))
			.endTime(LocalTime.of(18, 0))
			.description("성준's 가게")
			.businessLicenseImage(businessLicenseImg)
			.marketImage(marketImg)
			.build();

	@Nested
	@DisplayName("enrollMarket")
	class EnrollMarket {

		@Test
		@DisplayName("Success - 업체 신청에 성공한다 - enrollMarket")
		void enrollMarketSuccess() {
			// given
			Member member = new Member("google@gmail.com", USER, "1010");
			when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
			when(marketEnrollmentRepository.save(any(MarketEnrollment.class))).thenReturn(savedEnrollment);
			when(savedEnrollment.getId()).thenReturn(anyLong());

			// when
			enrollmentService.enrollMarket(request);

			// then
			verify(memberRepository).findById(1L);
			verify(marketEnrollmentRepository).save(any());
			verify(savedEnrollment).getId();
		}

		@Test
		@DisplayName("Fail - 존재하지 않는 회원에 대한 업체 신청은 실패한다")
		void enrollMarketFailByNotFound() {
			// given
			when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

			// when & then
			Assertions.assertThatThrownBy(() -> enrollmentService.enrollMarket(request))
					.isExactlyInstanceOf(BusinessException.class)
					.hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED);
		}

		@Test
		@DisplayName("Fail - 이미 업체인 회원에 대한 업체 신청은 실패한다")
		void enrollMarketFailByAlreadyMarket() {
			// given
			Member member = new Member("google@gmail.com", MARKET, "1010");
			when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

			// when & then
			Assertions.assertThatThrownBy(() -> enrollmentService.enrollMarket(request))
					.isExactlyInstanceOf(BusinessException.class)
					.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN);
		}
	}
}