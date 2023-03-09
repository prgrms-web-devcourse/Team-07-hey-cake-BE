package com.programmers.heycake.domain.market.controller;

import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.repository.ImageRepository;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentCreateRequest;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.util.TestUtils;

@Transactional
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class EnrollmentControllerTest {

	private static final String ACCESS_TOKEN = "access_token";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MarketEnrollmentRepository marketEnrollmentRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private MemberRepository memberRepository;

	private MockMultipartFile businessLicenseImg = TestUtils.getMockFile();
	private MockMultipartFile marketImg = TestUtils.getMockFile();
	private EnrollmentCreateRequest userRequest = EnrollmentCreateRequest.builder()
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
	@DisplayName("createEnrollment")
	@Transactional
	class CreateEnrollment {

		@Test
		@DisplayName("Success - 업체 신청에 성공하여 201 응답으로 성공한다")
		void createEnrollmentSuccess() throws Exception {
			// given
			Member member = TestUtils.getMember();
			memberRepository.save(member);

			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(
					new UsernamePasswordAuthenticationToken(member.getId(), null,
							List.of(new SimpleGrantedAuthority("ROLE_USER"))));

			// when
			MvcResult mvcResult = mockMvc.perform(multipart("/api/v1/enrollments")
							.file("businessLicenseImage", userRequest.businessLicenseImage().getBytes())
							.file("marketImage", userRequest.marketImage().getBytes())
							.header("access_token", ACCESS_TOKEN)
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
							requestHeaders(
									headerWithName("access_token").description("Access token 정보")
							),
							requestParts(
									partWithName("businessLicenseImage").description("사업자 등록증 이미지"),
									partWithName("marketImage").description("업체 대표 이미지")
							),
							requestParameters(
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
									parameterWithName("_csrf").description("토큰")
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

	@Test
	@DisplayName("Fail - 회원 인증에 실패하여 401 응답으로 실패한다")
	void createEnrollmentFailByUnAuthorized() throws Exception {
		MvcResult mvcResult = mockMvc.perform(multipart("/api/v1/enrollments")
						.file("businessLicenseImage", userRequest.businessLicenseImage().getBytes())
						.file("marketImage", userRequest.marketImage().getBytes())
						.header("access_token", ACCESS_TOKEN)
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
				.andExpect(status().isUnauthorized())
				.andDo(print())
				.andDo(document("MarketEnrollment/업체 신청 실패 - 회원 인증 실패",
						requestHeaders(
								headerWithName("access_token").description("Access token 정보")
						),
						requestParts(
								partWithName("businessLicenseImage").description("사업자 등록증 이미지"),
								partWithName("marketImage").description("업체 대표 이미지")
						),
						requestParameters(
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
								parameterWithName("_csrf").description("토큰")
						)))
				.andReturn();
	}

	@Test
	@DisplayName("Fail - 이미 업체인 유저가 업체 신청을 하면 403 응답으로 실패한다")
	void createEnrollmentFailByAlreadyMarket() throws Exception {
		// given
		Member marketMember = new Member("heycake@heycake.com", MARKET, "1010");
		memberRepository.save(marketMember);

		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(
				new UsernamePasswordAuthenticationToken(marketMember.getId(), null,
						List.of(new SimpleGrantedAuthority("ROLE_USER"))));

		// when & then
		MvcResult mvcResult = mockMvc.perform(multipart("/api/v1/enrollments")
						.file("businessLicenseImage", userRequest.businessLicenseImage().getBytes())
						.file("marketImage", userRequest.marketImage().getBytes())
						.header("access_token", ACCESS_TOKEN)
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
				.andExpect(status().isForbidden())
				.andDo(print())
				.andDo(document("MarketEnrollment/업체 신청 실패 - 이미 업체인 경우",
						requestHeaders(
								headerWithName("access_token").description("Access token 정보")
						),
						requestParts(
								partWithName("businessLicenseImage").description("사업자 등록증 이미지"),
								partWithName("marketImage").description("업체 대표 이미지")
						),
						requestParameters(
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
								parameterWithName("_csrf").description("토큰")
						)))
				.andReturn();
	}
}