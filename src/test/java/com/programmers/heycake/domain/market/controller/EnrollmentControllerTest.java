package com.programmers.heycake.domain.market.controller;

import static com.programmers.heycake.common.exception.ErrorCode.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;
import static com.programmers.heycake.domain.market.model.vo.EnrollmentStatus.*;
import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.MARKET;
import static com.programmers.heycake.domain.member.model.vo.MemberAuthority.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.repository.ImageRepository;
import com.programmers.heycake.domain.market.facade.EnrollmentFacade;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentCreateRequest;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentUpdateStatusRequest;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentDetailWithImageResponse;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentListSummaryWithImageResponse;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.market.repository.MarketRepository;
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
	private ObjectMapper objectMapper;

	@Autowired
	private EnrollmentController enrollmentController;

	@Autowired
	private EnrollmentFacade enrollmentFacade;

	@Autowired
	private MarketEnrollmentRepository marketEnrollmentRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MarketRepository marketRepository;

	private EnrollmentCreateRequest userRequest = TestUtils.getEnrollmentRequest("1234567890");

	@Nested
	@DisplayName("createEnrollment")
	@Transactional
	class CreateEnrollment {

		private Member member;

		@BeforeEach
		void setUp() {
			member = TestUtils.getMember();
			memberRepository.save(member);

			TestUtils.setContext(member.getId(), USER);
		}

		@Test
		@DisplayName("Success - 업체 신청에 성공하여 201 응답으로 성공한다")
		void createEnrollmentSuccess() throws Exception {
			// given & when
			MvcResult mvcResult = mockMvc.perform(
							multipart("/api/v1/enrollments").file("businessLicenseImage", userRequest.businessLicenseImage().getBytes())
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
									.param("description", userRequest.description()))
					.andExpect(status().isCreated())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 성공",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestParts(partWithName("businessLicenseImage").description("사업자 등록증 이미지"),
									partWithName("marketImage").description("업체 대표 이미지")),
							requestParameters(parameterWithName("businessNumber").description("사업자 등록 번호"),
									parameterWithName("ownerName").description("대표자 이름"),
									parameterWithName("openDate").description("개업 일자"),
									parameterWithName("marketName").description("상호명"),
									parameterWithName("phoneNumber").description("업체 전화번호"),
									parameterWithName("city").description("주소 시"), parameterWithName("district").description("주소 구"),
									parameterWithName("detailAddress").description("상세 주소"),
									parameterWithName("openTime").description("오픈 시간"), parameterWithName("endTime").description("마감 시간"),
									parameterWithName("description").description("업체 설명")),
							responseHeaders(headerWithName("Location").description("생성된 데이터 URI"))))
					.andReturn();

			// then
			List<MarketEnrollment> enrollments = marketEnrollmentRepository.findAll();
			MarketEnrollment savedEnrollment = enrollments.get(0);
			List<Image> images = imageRepository.findAll();
			String location = mvcResult.getResponse().getHeader("Location");

			assertThat(enrollments.size()).isEqualTo(1);
			assertThat(savedEnrollment).usingRecursiveComparison()
					.ignoringFields("id", "marketAddress", "enrollmentStatus", "member", "createdAt", "updatedAt", "deletedAt")
					.isEqualTo(userRequest);
			assertThat(savedEnrollment).hasFieldOrPropertyWithValue("marketAddress.city", userRequest.city())
					.hasFieldOrPropertyWithValue("marketAddress.district", userRequest.district())
					.hasFieldOrPropertyWithValue("marketAddress.detailAddress", userRequest.detailAddress())
					.hasFieldOrPropertyWithValue("enrollmentStatus", WAITING);
			assertThat(images.size()).isEqualTo(2);
			assertThat(location).isEqualTo("/api/v1/enrollments/" + savedEnrollment.getId());
		}

		@Test
		@DisplayName("Fail - 입력 요청 값이 잘못되면 400 응답으로 실패한다")
		void createEnrollmentFailByBadRequest() throws Exception {
			// given & when & then
			MvcResult mvcResult = mockMvc.perform(
							multipart("/api/v1/enrollments").file("businessLicenseImage", userRequest.businessLicenseImage().getBytes())
									.file("marketImage", userRequest.marketImage().getBytes())
									.header("access_token", ACCESS_TOKEN)
									.param("businessNumber", " ")
									.param("ownerName", " ")
									.param("openDate", userRequest.openDate().toString())
									.param("marketName", " ")
									.param("phoneNumber", " ")
									.param("city", " ")
									.param("district", " ")
									.param("detailAddress", " ")
									.param("openTime", userRequest.openTime().toString())
									.param("endTime", userRequest.endTime().toString())
									.param("description", " "))
					.andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 실패 - 입력 요청 값이 잘못된 경우",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestParts(partWithName("businessLicenseImage").description("사업자 등록증 이미지"),
									partWithName("marketImage").description("업체 대표 이미지")),
							requestParameters(parameterWithName("businessNumber").description("사업자 등록 번호"),
									parameterWithName("ownerName").description("대표자 이름"),
									parameterWithName("openDate").description("개업 일자"),
									parameterWithName("marketName").description("상호명"),
									parameterWithName("phoneNumber").description("업체 전화번호"),
									parameterWithName("city").description("주소 시"), parameterWithName("district").description("주소 구"),
									parameterWithName("detailAddress").description("상세 주소"),
									parameterWithName("openTime").description("오픈 시간"), parameterWithName("endTime").description("마감 시간"),
									parameterWithName("description").description("업체 설명")),
							responseFields(fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.ARRAY).description("검증 실패 에러 정보"),
									fieldWithPath("inputErrors[].field").type(JsonFieldType.STRING).description("검증 실패한 필드"),
									fieldWithPath("inputErrors[].rejectedValue").type(JsonFieldType.STRING).description("실패한 요청 값"),
									fieldWithPath("inputErrors[].message").type(JsonFieldType.STRING).description("검증 실패 예외 메세지"))))
					.andReturn();

			int size = marketEnrollmentRepository.findAll().size();
			assertThat(size).isZero();
			assertThat(mvcResult.getResolvedException()).isExactlyInstanceOf(BindException.class);
		}

		@Test
		@DisplayName("Fail - 현재 시각보다 개업일이 늦으면 400 응답으로 실패한다")
		void createEnrollmentFailByOpenDateAfterNow() throws Exception {
			// given
			LocalDate openDateAfterNow = LocalDate.now().plusDays(1);

			// when & then
			MvcResult mvcResult = mockMvc.perform(
							multipart("/api/v1/enrollments").file("businessLicenseImage", userRequest.businessLicenseImage().getBytes())
									.file("marketImage", userRequest.marketImage().getBytes())
									.header("access_token", ACCESS_TOKEN)
									.param("businessNumber", userRequest.businessNumber())
									.param("ownerName", userRequest.ownerName())
									.param("openDate", openDateAfterNow.toString())
									.param("marketName", userRequest.marketName())
									.param("phoneNumber", userRequest.phoneNumber())
									.param("city", userRequest.city())
									.param("district", userRequest.district())
									.param("detailAddress", userRequest.detailAddress())
									.param("openTime", userRequest.openTime().toString())
									.param("endTime", userRequest.endTime().toString())
									.param("description", userRequest.description()))
					.andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 실패 - 개업일이 현재 시각보다 늦은 경우",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestParts(partWithName("businessLicenseImage").description("사업자 등록증 이미지"),
									partWithName("marketImage").description("업체 대표 이미지")),
							requestParameters(parameterWithName("businessNumber").description("사업자 등록 번호"),
									parameterWithName("ownerName").description("대표자 이름"),
									parameterWithName("openDate").description("개업 일자"),
									parameterWithName("marketName").description("상호명"),
									parameterWithName("phoneNumber").description("업체 전화번호"),
									parameterWithName("city").description("주소 시"), parameterWithName("district").description("주소 구"),
									parameterWithName("detailAddress").description("상세 주소"),
									parameterWithName("openTime").description("오픈 시간"), parameterWithName("endTime").description("마감 시간"),
									parameterWithName("description").description("업체 설명")),
							responseFields(fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보"))))
					.andReturn();

			int size = marketEnrollmentRepository.findAll().size();
			assertThat(size).isZero();
			assertThat(mvcResult.getResolvedException()).isExactlyInstanceOf(BusinessException.class)
					.hasMessage(BAD_REQUEST.getMessage());
		}

		@Test
		@DisplayName("Fail - 회원 인증에 실패하여 401 응답으로 실패한다")
		void createEnrollmentFailByUnAuthorized() throws Exception {
			// given
			SecurityContextHolder.clearContext();

			// when & then
			mockMvc.perform(
							multipart("/api/v1/enrollments").file("businessLicenseImage", userRequest.businessLicenseImage().getBytes())
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
									.param("description", userRequest.description()))
					.andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 실패 - 회원 인증 실패",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestParts(partWithName("businessLicenseImage").description("사업자 등록증 이미지"),
									partWithName("marketImage").description("업체 대표 이미지")),
							requestParameters(parameterWithName("businessNumber").description("사업자 등록 번호"),
									parameterWithName("ownerName").description("대표자 이름"),
									parameterWithName("openDate").description("개업 일자"),
									parameterWithName("marketName").description("상호명"),
									parameterWithName("phoneNumber").description("업체 전화번호"),
									parameterWithName("city").description("주소 시"), parameterWithName("district").description("주소 구"),
									parameterWithName("detailAddress").description("상세 주소"),
									parameterWithName("openTime").description("오픈 시간"), parameterWithName("endTime").description("마감 시간"),
									parameterWithName("description").description("업체 설명")),
							responseFields(fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보"))));

			int size = marketEnrollmentRepository.findAll().size();
			assertThat(size).isZero();
		}

		@Test
		@DisplayName("Fail - 이미 업체인 유저가 업체 신청을 하면 403 응답으로 실패한다")
		void createEnrollmentFailByAlreadyMarket() throws Exception {
			// given
			Member marketMember = new Member("market@heycake.com", MARKET, "1010", "kwon");
			memberRepository.save(marketMember);

			SecurityContextHolder.clearContext();
			TestUtils.setContext(marketMember.getId(), MARKET);

			// when & then
			mockMvc.perform(
							multipart("/api/v1/enrollments").file("businessLicenseImage", userRequest.businessLicenseImage().getBytes())
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
									.param("description", userRequest.description()))
					.andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 실패 - 이미 업체인 경우",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestParts(partWithName("businessLicenseImage").description("사업자 등록증 이미지"),
									partWithName("marketImage").description("업체 대표 이미지")),
							requestParameters(parameterWithName("businessNumber").description("사업자 등록 번호"),
									parameterWithName("ownerName").description("대표자 이름"),
									parameterWithName("openDate").description("개업 일자"),
									parameterWithName("marketName").description("상호명"),
									parameterWithName("phoneNumber").description("업체 전화번호"),
									parameterWithName("city").description("주소 시"), parameterWithName("district").description("주소 구"),
									parameterWithName("detailAddress").description("상세 주소"),
									parameterWithName("openTime").description("오픈 시간"), parameterWithName("endTime").description("마감 시간"),
									parameterWithName("description").description("업체 설명")),
							responseFields(fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보"))));

			int size = marketEnrollmentRepository.findAll().size();
			assertThat(size).isZero();
		}
	}

	@Nested
	@DisplayName("getMarketEnrollment")
	@Transactional
	class GetMarketEnrollment {

		private Member adminMember;

		@BeforeEach
		void setUp() {
			adminMember = new Member("admin@heycake.com", ADMIN, "1010", "kwon");
			memberRepository.save(adminMember);

			TestUtils.setContext(adminMember.getId(), ADMIN);
		}

		@Test
		@DisplayName("Success - 업체 신청 상세 정보 조회를 하면 201로 응답으로 성공한다")
		void getMarketEnrollmentSuccess() throws Exception {
			// given
			Member member = TestUtils.getMember();
			memberRepository.save(member);

			MarketEnrollment enrollment = TestUtils.getMarketEnrollment("1234567890");
			enrollment.setMember(member);
			marketEnrollmentRepository.save(enrollment);

			Image image = new Image(enrollment.getId(), ENROLLMENT_MARKET, "imageUrl");
			imageRepository.save(image);

			EnrollmentDetailWithImageResponse enrollmentResponse = getEnrollmentResponse(enrollment, image);

			// when & then
			MvcResult mvcResult = mockMvc.perform(
							get("/api/v1/enrollments/{enrollmentId}", enrollment.getId()).header("access_token", ACCESS_TOKEN))
					.andExpect(status().isOk())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 상세 조회 성공",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							responseFields(fieldWithPath("phoneNumber").description("업체 전화번호"),
									fieldWithPath("marketAddress").description("업체 주소"),
									fieldWithPath("marketAddress.city").description("업체 주소 - 시"),
									fieldWithPath("marketAddress.district").description("업체 주소 -  구"),
									fieldWithPath("marketAddress.detailAddress").description("업체 주소 - 상세 주소"),
									fieldWithPath("openTime").description("오픈 시간"), fieldWithPath("endTime").description("마감 시간"),
									fieldWithPath("description").description("업체 설명"), fieldWithPath("marketName").description("상호명"),
									fieldWithPath("businessNumber").description("사업자 등록 번호"),
									fieldWithPath("ownerName").description("대표자 이름"),
									fieldWithPath("marketImage").description("업체 이미지 URL"))))
					.andReturn();

			JSONObject response = new JSONObject(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8));

			assertThat(response.getString("phoneNumber")).isEqualTo(enrollmentResponse.phoneNumber());
			assertThat(response.getJSONObject("marketAddress").getString("city")).isEqualTo(
					enrollmentResponse.marketAddress().getCity());
			assertThat(response.getJSONObject("marketAddress").getString("district")).isEqualTo(
					enrollmentResponse.marketAddress().getDistrict());
			assertThat(response.getJSONObject("marketAddress").getString("detailAddress")).isEqualTo(
					enrollmentResponse.marketAddress().getDetailAddress());
			assertThat(response.getString("openTime")).isEqualTo(enrollmentResponse.openTime().toString());
			assertThat(response.getString("endTime")).isEqualTo(enrollmentResponse.endTime().toString());
			assertThat(response.getString("description")).isEqualTo(enrollmentResponse.description());
			assertThat(response.getString("marketName")).isEqualTo(enrollmentResponse.marketName());
			assertThat(response.getString("businessNumber")).isEqualTo(enrollmentResponse.businessNumber());
			assertThat(response.getString("ownerName")).isEqualTo(enrollmentResponse.ownerName());
			assertThat(response.getString("marketImage")).isEqualTo(enrollmentResponse.marketImage());
		}

		// todo 업체 신청 조회 권한 permitAll -> Admin 으로 변경 시 활성화
		// @Test
		// @DisplayName("Fail - 회원 인증에 실패하여 401 응답으로 실패한다")
		// void getMarketEnrollmentFailByUnauthorized() throws Exception {
		// 	// given
		// 	Member member = TestUtils.getMember();
		// 	memberRepository.save(member);
		//
		// 	TestUtils.setContext(member.getId(), USER);
		//
		// 	Long enrollmentId = enrollmentFacade.createEnrollment(userRequest);
		//
		// 	SecurityContextHolder.clearContext();
		//
		// 	// when & then
		// 	MvcResult mvcResult = mockMvc.perform(get("/api/v1/enrollments/{enrollmentId}", enrollmentId)
		// 					.header("access_token", ACCESS_TOKEN))
		// 			.andExpect(status().isUnauthorized())
		// 			.andDo(print())
		// 			.andDo(document("MarketEnrollment/업체 신청 상세 조회 실패 - 회원 인증 실패",
		// 					requestHeaders(
		// 							headerWithName("access_token").description("Access token 정보")
		// 					),
		// 					responseFields(
		// 							fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
		// 							fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
		// 							fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
		// 							fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보")
		// 					)))
		// 			.andReturn();
		// }

		// todo 업체 신청 조회 권한 permitAll -> Admin 으로 변경 시 활성화
		// @Test
		// @DisplayName("Fail - 관리자가 아닌 회원이 요청을 하면 403 응답으로 실패한다")
		// void getMarketEnrollmentFailByNotAdmin() throws Exception {
		// 	// given
		// 	Member member = TestUtils.getMember();
		// 	memberRepository.save(member);
		//
		// 	TestUtils.setContext(member.getId(), USER);
		//
		// 	Long enrollmentId = enrollmentFacade.createEnrollment(userRequest);
		//
		// 	// when & then
		// 	MvcResult mvcResult = mockMvc.perform(get("/api/v1/enrollments/{enrollmentId}", enrollmentId)
		// 					.header("access_token", ACCESS_TOKEN))
		// 			.andExpect(status().isForbidden())
		// 			.andDo(print())
		// 			.andDo(document("MarketEnrollment/업체 신청 상세 조회 실패 - 관리자가 아닌 회원이 요청한 경우",
		// 					requestHeaders(
		// 							headerWithName("access_token").description("Access token 정보")
		// 					),
		// 					responseFields(
		// 							fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
		// 							fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
		// 							fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
		// 							fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보")
		// 					)))
		// 			.andReturn();
		// }

		@Test
		@DisplayName("Fail - 존재하지 않는 업체 신청 id 를 조회하면 404 응답으로 실패한다")
		void getMarketEnrollmentFailByNotFound() throws Exception {
			// given & when & then
			MvcResult mvcResult = mockMvc.perform(
							get("/api/v1/enrollments/{enrollmentId}", 0L).header("access_token", ACCESS_TOKEN))
					.andExpect(status().isNotFound())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 상세 조회 실패 - 존재하지 않는 id 인 경우",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							responseFields(fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보"))))
					.andReturn();

			int size = marketEnrollmentRepository.findAll().size();
			assertThat(size).isZero();
			assertThat(mvcResult.getResolvedException()).isExactlyInstanceOf(BusinessException.class)
					.hasMessage(ENTITY_NOT_FOUND.getMessage());
		}

		private EnrollmentDetailWithImageResponse getEnrollmentResponse(MarketEnrollment enrollment, Image image) {
			return EnrollmentDetailWithImageResponse.builder()
					.phoneNumber(enrollment.getPhoneNumber())
					.marketAddress(enrollment.getMarketAddress())
					.openTime(enrollment.getOpenTime())
					.endTime(enrollment.getEndTime())
					.description(enrollment.getDescription())
					.marketName(enrollment.getMarketName())
					.businessNumber(enrollment.getBusinessNumber())
					.ownerName(enrollment.getOwnerName())
					.marketImage(image.getImageUrl())
					.build();
		}
	}

	@Nested
	@DisplayName("changeEnrollmentStatus")
	@Transactional
	class ChangeEnrollmentStatus {

		private Member user;
		private Member adminMember;
		private MarketEnrollment enrollment;
		private MarketEnrollment savedEnrollment;

		@BeforeEach
		void setUp() {
			user = TestUtils.getMember();
			adminMember = new Member("admin@heycake.com", ADMIN, "1010", "kwon");
			memberRepository.save(user);
			memberRepository.save(adminMember);

			TestUtils.setContext(adminMember.getId(), ADMIN);

			enrollment = TestUtils.getMarketEnrollment("1234567890");
			enrollment.setMember(user);
			savedEnrollment = marketEnrollmentRepository.save(enrollment);
		}

		@Test
		@DisplayName("Success - 업체 신청을 승인하고 204로 응답한다")
		void changeEnrollmentStatusSuccessToApproved() throws Exception {
			// given
			EnrollmentUpdateStatusRequest approvedRequest = new EnrollmentUpdateStatusRequest(APPROVED);

			// when
			mockMvc.perform(
							patch("/api/v1/enrollments/{enrollmentId}", savedEnrollment.getId()).header("access_token", ACCESS_TOKEN)
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.writeValueAsString(approvedRequest)))
					.andExpect(status().isNoContent())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 승인 성공",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestFields(fieldWithPath("status").type(JsonFieldType.STRING).description("변경 후 상태"))));

			// then
			MarketEnrollment findEnrollment = marketEnrollmentRepository.findById(savedEnrollment.getId()).get();
			Market market = marketRepository.findByMember(user).get();
			assertThat(findEnrollment.getEnrollmentStatus()).isEqualTo(APPROVED);
			assertThat(user.getMemberAuthority()).isEqualTo(MARKET);
			assertThat(market.getMarketEnrollment()).isEqualTo(savedEnrollment);
		}

		@Test
		@DisplayName("Success - 업체 신청을 거절하고 204로 응답한다")
		void changeEnrollmentStatusSuccessToDeleted() throws Exception {
			// given
			EnrollmentUpdateStatusRequest deletedRequest = new EnrollmentUpdateStatusRequest(DELETED);

			// when
			mockMvc.perform(
							patch("/api/v1/enrollments/{enrollmentId}", savedEnrollment.getId()).header("access_token", ACCESS_TOKEN)
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.writeValueAsString(deletedRequest)))
					.andExpect(status().isNoContent())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 거절 성공",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestFields(fieldWithPath("status").type(JsonFieldType.STRING).description("요청 처리 후 상태"))));

			// then
			MarketEnrollment findEnrollment = marketEnrollmentRepository.findById(savedEnrollment.getId()).get();
			assertThat(findEnrollment.getEnrollmentStatus()).isEqualTo(DELETED);
			assertThat(user.getMemberAuthority()).isEqualTo(USER);
		}

		@Test
		@DisplayName("Success - 거절된 업체 신청을 승인 대기로 변경하고 204로 응답한다")
		void changeEnrollmentStatusSuccessToWaiting() throws Exception {
			// given
			enrollmentFacade.changeEnrollmentStatus(savedEnrollment.getId(), DELETED);
			EnrollmentUpdateStatusRequest waitingRequest = new EnrollmentUpdateStatusRequest(WAITING);

			// when
			mockMvc.perform(
							patch("/api/v1/enrollments/{enrollmentId}", savedEnrollment.getId()).header("access_token", ACCESS_TOKEN)
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.writeValueAsString(waitingRequest)))
					.andExpect(status().isNoContent())
					.andDo(print())
					.andDo(document("MarketEnrollment/거절된 업체 신청 승인 대기로 변경 성공",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestFields(fieldWithPath("status").type(JsonFieldType.STRING).description("요청 처리 후 상태"))));

			// then
			MarketEnrollment findEnrollment = marketEnrollmentRepository.findById(savedEnrollment.getId()).get();
			assertThat(findEnrollment.getEnrollmentStatus()).isEqualTo(WAITING);
		}

		@Test
		@DisplayName("Fail - 회원 인증에 실패하여 401 응답으로 실패한다")
		void changeEnrollmentStatusFailByUnauthorized() throws Exception {
			// given
			SecurityContextHolder.clearContext();
			EnrollmentUpdateStatusRequest approvedRequest = new EnrollmentUpdateStatusRequest(APPROVED);

			// when & then
			mockMvc.perform(
							patch("/api/v1/enrollments/{enrollmentId}", savedEnrollment.getId()).header("access_token", ACCESS_TOKEN)
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.writeValueAsString(approvedRequest)))
					.andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 상태 변경 실패 - 회원 인증 실패",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestFields(fieldWithPath("status").type(JsonFieldType.STRING).description("요청 처리 후 상태")),
							responseFields(fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보"))));

			assertThat(savedEnrollment.getEnrollmentStatus()).isEqualTo(WAITING);
		}

		@Test
		@DisplayName("Fail - 관리자가 아닌 회원이 요청하면 403 응답으로 실패한다")
		void changeEnrollmentStatusFailByForbidden() throws Exception {
			// given
			SecurityContextHolder.clearContext();
			TestUtils.setContext(user.getId(), USER);
			EnrollmentUpdateStatusRequest approvedRequest = new EnrollmentUpdateStatusRequest(APPROVED);

			// when & then
			mockMvc.perform(
							patch("/api/v1/enrollments/{enrollmentId}", savedEnrollment.getId()).header("access_token", ACCESS_TOKEN)
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.writeValueAsString(approvedRequest)))
					.andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 상태 변경 실패 - 관리자가 아닌 회원의 요청",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestFields(fieldWithPath("status").type(JsonFieldType.STRING).description("요청 처리 후 상태")),
							responseFields(fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보"))));

			assertThat(savedEnrollment.getEnrollmentStatus()).isEqualTo(WAITING);
		}

		@Test
		@DisplayName("Fail - 존재하지 않는 업체 신청 상태를 변경하면 404 로 응답하며 실패한다")
		void changeEnrollmentStatusFailByNotFound() throws Exception {
			// given
			EnrollmentUpdateStatusRequest approvedRequest = new EnrollmentUpdateStatusRequest(APPROVED);

			// when & then
			MvcResult mvcResult = mockMvc.perform(
							patch("/api/v1/enrollments/{enrollmentId}", 0L).header("access_token", ACCESS_TOKEN)
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.writeValueAsString(approvedRequest)))
					.andExpect(status().isNotFound())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 상태 변경 실패 - 존재하지 않는 업체 신청인 경우",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestFields(fieldWithPath("status").type(JsonFieldType.STRING).description("요청 처리 후 상태")),
							responseFields(fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보"))))
					.andReturn();

			assertThat(mvcResult.getResolvedException()).isExactlyInstanceOf(BusinessException.class)
					.hasMessage(ENTITY_NOT_FOUND.getMessage());
		}

		@Test
		@DisplayName("Fail - 현재 상태와 같은 상태로 변경하면 409 로 응답하며 실패한다")
		void changeEnrollmentStatusFailByDuplicated() throws Exception {
			// given
			EnrollmentUpdateStatusRequest waitingRequest = new EnrollmentUpdateStatusRequest(WAITING);

			// when & then
			MvcResult mvcResult = mockMvc.perform(
							patch("/api/v1/enrollments/{enrollmentId}", savedEnrollment.getId()).header("access_token", ACCESS_TOKEN)
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.writeValueAsString(waitingRequest)))
					.andExpect(status().isConflict())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 상태 변경 실패 - 현재와 같은 상태로의 변경 요청인 경우",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestFields(fieldWithPath("status").type(JsonFieldType.STRING).description("요청 처리 후 상태")),
							responseFields(fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보"))))
					.andReturn();

			assertThat(savedEnrollment.getEnrollmentStatus()).isEqualTo(WAITING);
			assertThat(mvcResult.getResolvedException()).isExactlyInstanceOf(BusinessException.class)
					.hasMessage(DUPLICATED.getMessage());
		}

		@Test
		@DisplayName("Fail - 이미 업체인 회원의 업체 신청을 승인하면 409 응답하며 실패한다")
		void changeEnrollmentStatusFailByAlreadyMarket() throws Exception {
			// given
			EnrollmentUpdateStatusRequest approvedRequest = new EnrollmentUpdateStatusRequest(APPROVED);
			enrollmentController.changeEnrollmentStatus(savedEnrollment.getId(), approvedRequest);

			// when & then
			MvcResult mvcResult = mockMvc.perform(
							patch("/api/v1/enrollments/{enrollmentId}", savedEnrollment.getId()).header("access_token", ACCESS_TOKEN)
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.writeValueAsString(approvedRequest)))
					.andExpect(status().isConflict())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 상태 변경 실패 - 이미 업체인 회원의 승인 요청인 경우",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestFields(fieldWithPath("status").type(JsonFieldType.STRING).description("요청 처리 후 상태")),
							responseFields(fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보"))))
					.andReturn();

			assertThat(mvcResult.getResolvedException()).isExactlyInstanceOf(BusinessException.class)
					.hasMessage(DUPLICATED.getMessage());
		}
	}

	@Nested
	@DisplayName("getMarketEnrollments")
	@Transactional
	class GetMarketEnrollments {

		private Member member;

		@BeforeEach
		void setUp() {
			member = new Member("admin@heycake.com", ADMIN, "1010", "kwon");
			memberRepository.save(member);

			TestUtils.setContext(member.getId(), ADMIN);
		}

		@Test
		@DisplayName("Success - 업체 신청 목록 조회에 성공하여 200 응답한다")
		void getMarketEnrollmentsSuccess() throws Exception {
			// given
			Member user = TestUtils.getMember();
			memberRepository.save(user);

			MarketEnrollment enrollment1 = TestUtils.getMarketEnrollment("1234567891");
			MarketEnrollment enrollment2 = TestUtils.getMarketEnrollment("1234567892");
			MarketEnrollment enrollment3 = TestUtils.getMarketEnrollment("1234567893");
			enrollment1.setMember(user);
			enrollment2.setMember(user);
			enrollment3.setMember(user);
			marketEnrollmentRepository.saveAll(List.of(enrollment1, enrollment2, enrollment3));

			Image image1 = new Image(enrollment1.getId(), ENROLLMENT_MARKET, "imageUrl");
			Image image2 = new Image(enrollment2.getId(), ENROLLMENT_MARKET, "imageUrl");
			Image image3 = new Image(enrollment3.getId(), ENROLLMENT_MARKET, "imageUrl");
			imageRepository.saveAll(List.of(image1, image2, image3));

			List<EnrollmentListSummaryWithImageResponse> enrollmentResponses = List.of(
					getEnrollmentResponse(enrollment2, image2),
					getEnrollmentResponse(enrollment1, image1)
			);

			int pageSize = 2;

			// when & then
			MvcResult mvcResult = mockMvc.perform(get("/api/v1/enrollments").header("access_token", ACCESS_TOKEN)
							.queryParam("cursorId", enrollment3.getId().toString())
							.queryParam("pageSize", String.valueOf(pageSize))
							.queryParam("status", WAITING.toString()))
					.andExpect(status().isOk())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 목록 조회 성공",
							requestHeaders(headerWithName("access_token").description("Access token 정보")),
							requestParameters(parameterWithName("cursorId").optional().description("목록의 시작이 되는 업체 신청 id"),
									parameterWithName("pageSize").description("한번에 조회할 데이터 수"),
									parameterWithName("status").optional().description("조회하고자 하는 업체 신청의 상태")),
							responseFields(fieldWithPath("enrollments").type(JsonFieldType.ARRAY).description("업체 신청 정보의 배열"),
									fieldWithPath("enrollments[].enrollmentId").type(JsonFieldType.NUMBER).description("업체 신청 id"),
									fieldWithPath("enrollments[].imageUrl").type(JsonFieldType.STRING).description("업체 이미지 URL"),
									fieldWithPath("enrollments[].businessNumber").type(JsonFieldType.STRING).description("사업자 등록 번호"),
									fieldWithPath("enrollments[].address").type(JsonFieldType.OBJECT).description("주소"),
									fieldWithPath("enrollments[].address.city").type(JsonFieldType.STRING).description("주소 - 시"),
									fieldWithPath("enrollments[].address.district").type(JsonFieldType.STRING).description("주소 - 구"),
									fieldWithPath("enrollments[].address.detailAddress").type(JsonFieldType.STRING)
											.description("주소 - 상세 주소"),
									fieldWithPath("enrollments[].marketName").type(JsonFieldType.STRING).description("업체명"),
									fieldWithPath("enrollments[].phoneNumber").type(JsonFieldType.STRING).description("업체 전화번호"),
									fieldWithPath("enrollments[].ownerName").type(JsonFieldType.STRING).description("업체 사장님 이름"),
									fieldWithPath("enrollments[].status").type(JsonFieldType.STRING).description("업체 신청 진행 상태"),
									fieldWithPath("enrollments[].createdAt").type(JsonFieldType.STRING).description("신청 시간"),
									fieldWithPath("nextCursor").type(JsonFieldType.NUMBER).description("다음 목록의 시작이 되는 업체 신청 id"))))
					.andReturn();

			JSONObject response = new JSONObject(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8));
			JSONArray enrollments = response.getJSONArray("enrollments");

			for (int idx = 0; idx < enrollmentResponses.size(); idx++) {
				JSONObject enrollment = enrollments.getJSONObject(idx);
				assertThat(enrollment.getLong("enrollmentId")).isEqualTo(enrollmentResponses.get(idx).enrollmentId());
				assertThat(enrollment.getString("imageUrl")).isEqualTo(enrollmentResponses.get(idx).imageUrl());
				assertThat(enrollment.getString("businessNumber")).isEqualTo(enrollmentResponses.get(idx).businessNumber());
				assertThat(enrollment.getJSONObject("address").getString("city")).isEqualTo(
						enrollmentResponses.get(idx).address().getCity());
				assertThat(enrollment.getJSONObject("address").getString("district")).isEqualTo(
						enrollmentResponses.get(idx).address().getDistrict());
				assertThat(enrollment.getJSONObject("address").getString("detailAddress")).isEqualTo(
						enrollmentResponses.get(idx).address().getDetailAddress());
				assertThat(enrollment.getString("marketName")).isEqualTo(enrollmentResponses.get(idx).marketName());
				assertThat(enrollment.getString("phoneNumber")).isEqualTo(enrollmentResponses.get(idx).phoneNumber());
				assertThat(enrollment.getString("ownerName")).isEqualTo(enrollmentResponses.get(idx).ownerName());
				assertThat(enrollment.getString("status")).isEqualTo(enrollmentResponses.get(idx).status().toString());
				assertThat(enrollment.getString("createdAt"))
						.isEqualTo(enrollmentResponses.get(idx).createdAt()
								.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			}
		}

		// todo 업체 신청 조회 권한 permitAll -> Admin 으로 변경 시 활성화
		// @Test
		// @DisplayName("Fail - 회원 인증 실패로 업체 신청 목록 조회에 실패하여 401 응답한다")
		// void getMarketEnrollmentsFailByUnauthorized() throws Exception {
		// 	// given
		// 	EnrollmentCreateRequest request1 = TestUtils.getEnrollmentRequest("1234567891");
		// 	EnrollmentCreateRequest request2 = TestUtils.getEnrollmentRequest("1234567892");
		// 	EnrollmentCreateRequest request3 = TestUtils.getEnrollmentRequest("1234567893");
		// 	Long cursorId = enrollmentFacade.createEnrollment(request1);
		// 	enrollmentFacade.createEnrollment(request2);
		// 	enrollmentFacade.createEnrollment(request3);
		//
		// 	SecurityContextHolder.clearContext();
		//
		// 	// when & then
		// 	mockMvc.perform(get("/api/v1/enrollments")
		// 					.header("access_token", ACCESS_TOKEN)
		// 					.queryParam("cursorId", cursorId.toString())
		// 					.queryParam("pageSize", "10")
		// 					.queryParam("status", WAITING.toString()))
		// 			.andExpect(status().isUnauthorized())
		// 			.andDo(print())
		// 			.andDo(document("MarketEnrollment/업체 신청 목록 조회 실패 - 회원 인증 실패",
		// 					requestHeaders(
		// 							headerWithName("access_token").description("Access token 정보")
		// 					),
		// 					requestParameters(
		// 							parameterWithName("cursorId").optional().description("목록의 시작이 되는 업체 신청 id"),
		// 							parameterWithName("pageSize").description("한번에 조회할 데이터 수"),
		// 							parameterWithName("status").optional().description("조회하고자 하는 업체 신청의 상태")
		// 					),
		// 					responseFields(
		// 							fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
		// 							fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
		// 							fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
		// 							fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보")
		// 					)))
		// 			.andReturn();
		// }

		// todo 업체 신청 조회 권한 permitAll -> Admin 으로 변경 시 활성화
		// @Test
		// @DisplayName("Fail - 관리자가 아닌 회원이 업체 신청 목록을 조회하면 실패하며 403 응답한다")
		// void getMarketEnrollmentsFailByForbidden() throws Exception {
		// 	// given
		// 	EnrollmentCreateRequest request1 = TestUtils.getEnrollmentRequest("1234567891");
		// 	EnrollmentCreateRequest request2 = TestUtils.getEnrollmentRequest("1234567892");
		// 	EnrollmentCreateRequest request3 = TestUtils.getEnrollmentRequest("1234567893");
		// 	Long cursorId = enrollmentFacade.createEnrollment(request1);
		// 	enrollmentFacade.createEnrollment(request2);
		// 	enrollmentFacade.createEnrollment(request3);
		//
		// 	member = TestUtils.getMember();
		// 	memberRepository.save(member);
		//
		// 	SecurityContextHolder.clearContext();
		// 	TestUtils.setContext(member.getId(), USER);
		//
		// 	// when & then
		// 	mockMvc.perform(get("/api/v1/enrollments")
		// 					.header("access_token", ACCESS_TOKEN)
		// 					.queryParam("cursorId", cursorId.toString())
		// 					.queryParam("pageSize", "10")
		// 					.queryParam("status", WAITING.toString()))
		// 			.andExpect(status().isUnauthorized())
		// 			.andDo(print())
		// 			.andDo(document("MarketEnrollment/업체 신청 목록 조회 실패 - 회원 인증 실패",
		// 					requestHeaders(
		// 							headerWithName("access_token").description("Access token 정보")
		// 					),
		// 					requestParameters(
		// 							parameterWithName("cursorId").optional().description("목록의 시작이 되는 업체 신청 id"),
		// 							parameterWithName("pageSize").description("한번에 조회할 데이터 수"),
		// 							parameterWithName("status").optional().description("조회하고자 하는 업체 신청의 상태")
		// 					),
		// 					responseFields(
		// 							fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
		// 							fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
		// 							fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
		// 							fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보")
		// 					)))
		// 			.andReturn();
		// }

		private EnrollmentListSummaryWithImageResponse getEnrollmentResponse(MarketEnrollment enrollment, Image image) {
			return EnrollmentListSummaryWithImageResponse.builder()
					.enrollmentId(enrollment.getId())
					.imageUrl(image.getImageUrl())
					.businessNumber(enrollment.getBusinessNumber())
					.address(enrollment.getMarketAddress())
					.marketName(enrollment.getMarketName())
					.phoneNumber(enrollment.getPhoneNumber())
					.ownerName(enrollment.getOwnerName())
					.status(enrollment.getEnrollmentStatus())
					.createdAt(enrollment.getCreatedAt())
					.build();
		}
	}
}