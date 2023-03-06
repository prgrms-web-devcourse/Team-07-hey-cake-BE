package com.programmers.heycake.domain.market.controller;

import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.programmers.heycake.common.config.S3MockConfig;
import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.repository.ImageRepository;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentCreateRequest;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;

import io.findify.s3mock.S3Mock;

@Import(S3MockConfig.class)
@ExtendWith(RestDocumentationExtension.class)
@WithMockUser
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class EnrollmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MarketEnrollmentRepository marketEnrollmentRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private MemberRepository memberRepository;

	private MockMultipartFile businessLicenseImg;
	private MockMultipartFile marketImg;
	private EnrollmentCreateRequest userRequest;

	@BeforeEach
	void setUp() {
		Member user = new Member("google@gmail.com", USER, "1010");
		Member savedUser = memberRepository.save(user);

		businessLicenseImg = new MockMultipartFile(
				"businessLicense",
				"businessLicense",
				".jpg",
				"businessLicense".getBytes()
		);
		marketImg = new MockMultipartFile(
				"market",
				"market",
				".jpg",
				"market".getBytes()
		);
		userRequest = EnrollmentCreateRequest.builder()
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
	}

	@AfterEach
	void clear() {
		marketEnrollmentRepository.deleteAll();
		imageRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@AfterAll
	static void tearDown(@Autowired S3Mock s3Mock, @Autowired AmazonS3 amazonS3) {
		amazonS3.shutdown();
		s3Mock.stop();
	}

	// todo 회원 인증 관련 로직 완성 시 예외 문서화 추가
	@Nested
	@DisplayName("enrollMarket")
	class EnrollMarket {

		@Test
		@DisplayName("Success - 업체 신청에 성공하여 201 응답한다")
		void enrollMarketSuccess() throws Exception {
			// given & when
			MvcResult mvcResult = mockMvc.perform(multipart("/api/v1/enrollments")
							.file("businessLicenseImage", userRequest.businessLicenseImage().getBytes())
							.file("marketImage", userRequest.marketImage().getBytes())
							.param("businessNumber", userRequest.businessNumber())
							.param("ownerName", userRequest.ownerName())
							.param("openDate", userRequest.openDate().toString())
							.param("marketName", userRequest.marketName())
							.param("phoneNumber", userRequest.phoneNumber())
							.param("city", userRequest.city())
							.param("district", userRequest.district())
							.param("detailAddress", userRequest.detailAddress())
							.param("openTime", userRequest.openTime().toString())
							.param("endTime", userRequest.endTime().toString())
							.param("description", userRequest.description())
							.with(csrf()))
					.andExpect(status().isCreated())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 성공",
							requestParts(
									partWithName("businessLicenseImage").description("사업자 등록증 이미지"),
									partWithName("marketImage").description("업체 대표 이미지")
							),
							requestParameters(
									parameterWithName("memberId").description("회원 id"),
									parameterWithName("businessNumber").description("사업자 등록 번호"),
									parameterWithName("ownerName").description("대표자 이름"),
									parameterWithName("openDate").description("개업 일자"),
									parameterWithName("marketName").description("상호명"),
									parameterWithName("phoneNumber").description("업체 전화번호"),
									parameterWithName("city").description("주소 시"),
									parameterWithName("district").description("주소 구"),
									parameterWithName("detailAddress").description("상세 주소"),
									parameterWithName("openTime").description("오픈 시간"),
									parameterWithName("endTime").description("마감 시간"),
									parameterWithName("description").description("업체 설명"),
									parameterWithName("_csrf").description("csrf 토큰")
							),
							responseHeaders(
									headerWithName("Location").description("생성된 데이터 URI")
							)))
					.andReturn();

			// then
			List<MarketEnrollment> enrollments = marketEnrollmentRepository.findAll();
			MarketEnrollment savedEnrollment = enrollments.get(0);
			List<Image> images = imageRepository.findAll();
			String location = mvcResult.getResponse().getHeader("Location");

			assertThat(enrollments.size()).isEqualTo(1);
			assertThat(savedEnrollment)
					.hasFieldOrPropertyWithValue("businessNumber", userRequest.businessNumber())
					.hasFieldOrPropertyWithValue("ownerName", userRequest.ownerName())
					.hasFieldOrPropertyWithValue("openDate", userRequest.openDate())
					.hasFieldOrPropertyWithValue("marketName", userRequest.marketName())
					.hasFieldOrPropertyWithValue("phoneNumber", userRequest.phoneNumber())
					.hasFieldOrPropertyWithValue("marketAddress.city", userRequest.city())
					.hasFieldOrPropertyWithValue("marketAddress.district", userRequest.district())
					.hasFieldOrPropertyWithValue("marketAddress.detailAddress", userRequest.detailAddress())
					.hasFieldOrPropertyWithValue("openTime", userRequest.openTime())
					.hasFieldOrPropertyWithValue("endTime", userRequest.endTime())
					.hasFieldOrPropertyWithValue("description", userRequest.description());
			assertThat(images.size()).isEqualTo(2);
			assertThat(location).isEqualTo("/api/v1/enrollments/" + savedEnrollment.getId());
		}
	}
}