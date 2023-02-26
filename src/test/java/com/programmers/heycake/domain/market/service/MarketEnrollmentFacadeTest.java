package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.market.model.dto.MarketEnrollmentRequest;

@ExtendWith(MockitoExtension.class)
class MarketEnrollmentFacadeTest {

	@Mock
	private MarketEnrollmentService marketEnrollmentService;

	@Mock
	private ImageIntegrationService imageIntegrationService;

	@InjectMocks
	private MarketEnrollmentFacade marketEnrollmentFacade;

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
	private MarketEnrollmentRequest request = MarketEnrollmentRequest.builder()
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

	@Test
	@DisplayName("Success - 업체 신청, 이미지 DB 저장과 이미지 업로드에 성공한다 - enrollMarket")
	void enrollMarketSuccess() {
		// given
		Long enrollmentId = 1L;
		String subPath = "image/marketEnrollment";
		when(marketEnrollmentService.enrollMarket(request)).thenReturn(enrollmentId);
		doNothing().when(imageIntegrationService)
				.createAndUploadImage(request.businessLicenseImage(), subPath, enrollmentId, ENROLLMENT_LICENSE);
		doNothing().when(imageIntegrationService)
				.createAndUploadImage(request.marketImage(), subPath, enrollmentId, ENROLLMENT_MARKET);

		// when
		marketEnrollmentFacade.enrollMarket(request);

		// then
		verify(marketEnrollmentService).enrollMarket(request);
		verify(imageIntegrationService)
				.createAndUploadImage(request.businessLicenseImage(), subPath, enrollmentId, ENROLLMENT_LICENSE);
		verify(imageIntegrationService)
				.createAndUploadImage(request.marketImage(), subPath, enrollmentId, ENROLLMENT_MARKET);
	}
}