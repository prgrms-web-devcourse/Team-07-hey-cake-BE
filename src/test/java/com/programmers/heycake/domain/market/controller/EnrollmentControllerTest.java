package com.programmers.heycake.domain.market.controller;

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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.repository.ImageRepository;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentCreateRequest;
import com.programmers.heycake.domain.market.model.dto.response.EnrollmentListSummaryWithImageResponse;
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

	private EnrollmentCreateRequest userRequest = TestUtils.getEnrollmentRequest("1234567890");

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

			TestUtils.setContext(member.getId(), USER);

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
							.param("description", userRequest.description()))
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
									parameterWithName("description").description("업체 설명")
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
					.usingRecursiveComparison()
					.ignoringFields(
							"id", "marketAddress", "enrollmentStatus", "member", "createdAt", "updatedAt", "deletedAt"
					)
					.isEqualTo(userRequest);
			assertThat(savedEnrollment)
					.hasFieldOrPropertyWithValue("marketAddress.city", userRequest.city())
					.hasFieldOrPropertyWithValue("marketAddress.district", userRequest.district())
					.hasFieldOrPropertyWithValue("marketAddress.detailAddress", userRequest.detailAddress())
					.hasFieldOrPropertyWithValue("enrollmentStatus", WAITING);
			assertThat(images.size()).isEqualTo(2);
			assertThat(location).isEqualTo("/api/v1/enrollments/" + savedEnrollment.getId());
		}

		@Test
		@DisplayName("Fail - 입력 요청 값이 잘못되면 400 응답으로 실패한다")
		void createEnrollmentFailByBadRequest() throws Exception {
			// given
			Member member = TestUtils.getMember();
			memberRepository.save(member);

			TestUtils.setContext(member.getId(), USER);

			// when & then
			MvcResult mvcResult = mockMvc.perform(multipart("/api/v1/enrollments")
							.file("businessLicenseImage", userRequest.businessLicenseImage().getBytes())
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
									parameterWithName("description").description("업체 설명")
							),
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.ARRAY).description("검증 실패 에러 정보"),
									fieldWithPath("inputErrors[].field").type(JsonFieldType.STRING).description("검증 실패한 필드"),
									fieldWithPath("inputErrors[].rejectedValue").type(JsonFieldType.STRING).description("실패한 요청 값"),
									fieldWithPath("inputErrors[].message").type(JsonFieldType.STRING).description("검증 실패 예외 메세지")
							)))
					.andReturn();
		}

		@Test
		@DisplayName("Fail - 현재 시각보다 개업일이 늦으면 400 응답으로 실패한다")
		void createEnrollmentFailByOpenDateAfterNow() throws Exception {
			// given
			LocalDate openDateAfterNow = LocalDate.now().plusDays(1);
			Member member = TestUtils.getMember();
			memberRepository.save(member);

			TestUtils.setContext(member.getId(), USER);

			// when & then
			MvcResult mvcResult = mockMvc.perform(multipart("/api/v1/enrollments")
							.file("businessLicenseImage", userRequest.businessLicenseImage().getBytes())
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
									parameterWithName("description").description("업체 설명")
							),
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보")
							)))
					.andReturn();
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
							.param("description", userRequest.description()))
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
									parameterWithName("description").description("업체 설명")
							),
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보")
							)))
					.andReturn();
		}

		@Test
		@DisplayName("Fail - 이미 업체인 유저가 업체 신청을 하면 403 응답으로 실패한다")
		void createEnrollmentFailByAlreadyMarket() throws Exception {
			// given
			Member marketMember = new Member("heycake@heycake.com", MARKET, "1010", "kwon");
			memberRepository.save(marketMember);

			TestUtils.setContext(marketMember.getId(), MARKET);

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
							.param("description", userRequest.description()))
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
									parameterWithName("description").description("업체 설명")
							),
							responseFields(
									fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
									fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
									fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
									fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보")
							)))
					.andReturn();
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
			MarketEnrollment enrollment1 = TestUtils.getMarketEnrollment("1234567891");
			MarketEnrollment enrollment2 = TestUtils.getMarketEnrollment("1234567892");
			MarketEnrollment enrollment3 = TestUtils.getMarketEnrollment("1234567893");
			enrollment1.setMember(member);
			enrollment2.setMember(member);
			enrollment3.setMember(member);
			marketEnrollmentRepository.saveAll(List.of(enrollment1, enrollment2, enrollment3));

			Image image1 = new Image(enrollment1.getId(), ENROLLMENT_MARKET, "imageUrl");
			Image image2 = new Image(enrollment2.getId(), ENROLLMENT_MARKET, "imageUrl");
			Image image3 = new Image(enrollment3.getId(), ENROLLMENT_MARKET, "imageUrl");
			imageRepository.saveAll(List.of(image1, image2, image3));

			List<EnrollmentListSummaryWithImageResponse> enrollmentResponses = List.of(
					getEnrollmentResponse(enrollment1, image1),
					getEnrollmentResponse(enrollment2, image2),
					getEnrollmentResponse(enrollment3, image3)
			);

			// when & then
			MvcResult mvcResult = mockMvc.perform(
							get("/api/v1/enrollments")
									.header("access_token", ACCESS_TOKEN)
									.queryParam("cursorId", enrollment1.getId().toString())
									.queryParam("pageSize", "2")
									.queryParam("status", WAITING.toString()))
					.andExpect(status().isOk())
					.andDo(print())
					.andDo(document("MarketEnrollment/업체 신청 목록 조회 성공",
							requestHeaders(
									headerWithName("access_token").description("Access token 정보")
							),
							requestParameters(
									parameterWithName("cursorId").optional().description("목록의 시작이 되는 업체 신청 id"),
									parameterWithName("pageSize").description("한번에 조회할 데이터 수"),
									parameterWithName("status").optional().description("조회하고자 하는 업체 신청의 상태")
							),
							responseFields(
									fieldWithPath("enrollments").type(JsonFieldType.ARRAY).description("업체 신청 정보의 배열"),
									fieldWithPath("enrollments[].enrollmentId").type(JsonFieldType.NUMBER).description("업체 신청 id"),
									fieldWithPath("enrollments[].imageUrl").type(JsonFieldType.STRING).description("업체 이미지 URL"),
									fieldWithPath("enrollments[].enrollmentId").type(JsonFieldType.STRING).description("사업자 등록 번호"),
									fieldWithPath("enrollments[].address").type(JsonFieldType.OBJECT).description("주소"),
									fieldWithPath("enrollments[].address.city")
											.type(JsonFieldType.STRING).description("주소 - 시"),
									fieldWithPath("enrollments[].address.district")
											.type(JsonFieldType.STRING).description("주소 - 구"),
									fieldWithPath("enrollments[].address.detailAddress")
											.type(JsonFieldType.STRING).description("주소 - 상세 주소"),
									fieldWithPath("enrollments[].marketName").type(JsonFieldType.STRING).description("업체명"),
									fieldWithPath("enrollments[].phoneNumber").type(JsonFieldType.STRING).description("업체 전화번호"),
									fieldWithPath("enrollments[].ownerName").type(JsonFieldType.STRING).description("업체 사장님 이름"),
									fieldWithPath("enrollments[].status").type(JsonFieldType.STRING).description("업체 신청 진행 상태"),
									fieldWithPath("enrollments[].createdAt").type(JsonFieldType.STRING).description("신청 시간"),
									fieldWithPath("nextCursor").type(JsonFieldType.NUMBER).description("다음 목록의 시작이 되는 업체 신청 id")
							)))
					.andReturn();

			JSONArray enrollments = new JSONArray(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8));
			for (int idx = 0; idx < enrollmentResponses.size(); idx++) {
				JSONObject enrollment = enrollments.getJSONObject(idx);
				assertThat(enrollment).usingRecursiveComparison().isEqualTo(enrollmentResponses.get(idx));
			}
		}

		@Test
		@DisplayName("Fail - 회원 인증 실패로 업체 신청 목록 조회에 실패하여 401 응답한다")
		void getMarketEnrollmentsFailByUnauthorized() throws Exception {
			// given
			// todo 업체 신청 조회 권한 permitAll -> Admin 으로 변경 시 활성화
			// EnrollmentCreateRequest request1 = TestUtils.getEnrollmentRequest("1234567891");
			// EnrollmentCreateRequest request2 = TestUtils.getEnrollmentRequest("1234567892");
			// EnrollmentCreateRequest request3 = TestUtils.getEnrollmentRequest("1234567893");
			// Long cursorId = enrollmentFacade.createEnrollment(request1);
			// enrollmentFacade.createEnrollment(request2);
			// enrollmentFacade.createEnrollment(request3);
			//
			// SecurityContextHolder.clearContext();
			//
			// // when & then
			// mockMvc.perform(get("/api/v1/enrollments")
			// 				.header("access_token", ACCESS_TOKEN)
			// 				.queryParam("cursorId", cursorId.toString())
			// 				.queryParam("pageSize", "10")
			// 				.queryParam("status", WAITING.toString()))
			// 		.andExpect(status().isUnauthorized())
			// 		.andDo(print())
			// 		.andDo(document("MarketEnrollment/업체 신청 목록 조회 실패 - 회원 인증 실패",
			// 				requestHeaders(
			// 						headerWithName("access_token").description("Access token 정보")
			// 				),
			// 				requestParameters(
			// 						parameterWithName("cursorId").optional().description("목록의 시작이 되는 업체 신청 id"),
			// 						parameterWithName("pageSize").description("한번에 조회할 데이터 수"),
			// 						parameterWithName("status").optional().description("조회하고자 하는 업체 신청의 상태")
			// 				),
			// 				responseFields(
			// 						fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
			// 						fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
			// 						fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
			// 						fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보")
			// 				)))
			// 		.andReturn();
		}

		@Test
		@DisplayName("Fail - 관리자가 아닌 회원이 업체 신청 목록을 조회하면 실패하며 403 응답한다")
		void getMarketEnrollmentsFailByForbidden() throws Exception {
			// given
			// todo 업체 신청 조회 권한 permitAll -> Admin 으로 변경 시 활성화
			// EnrollmentCreateRequest request1 = TestUtils.getEnrollmentRequest("1234567891");
			// EnrollmentCreateRequest request2 = TestUtils.getEnrollmentRequest("1234567892");
			// EnrollmentCreateRequest request3 = TestUtils.getEnrollmentRequest("1234567893");
			// Long cursorId = enrollmentFacade.createEnrollment(request1);
			// enrollmentFacade.createEnrollment(request2);
			// enrollmentFacade.createEnrollment(request3);
			//
			// member = TestUtils.getMember();
			// memberRepository.save(member);
			//
			// SecurityContextHolder.clearContext();
			// TestUtils.setContext(member.getId(), USER);
			//
			// // when & then
			// mockMvc.perform(get("/api/v1/enrollments")
			// 				.header("access_token", ACCESS_TOKEN)
			// 				.queryParam("cursorId", cursorId.toString())
			// 				.queryParam("pageSize", "10")
			// 				.queryParam("status", WAITING.toString()))
			// 		.andExpect(status().isUnauthorized())
			// 		.andDo(print())
			// 		.andDo(document("MarketEnrollment/업체 신청 목록 조회 실패 - 회원 인증 실패",
			// 				requestHeaders(
			// 						headerWithName("access_token").description("Access token 정보")
			// 				),
			// 				requestParameters(
			// 						parameterWithName("cursorId").optional().description("목록의 시작이 되는 업체 신청 id"),
			// 						parameterWithName("pageSize").description("한번에 조회할 데이터 수"),
			// 						parameterWithName("status").optional().description("조회하고자 하는 업체 신청의 상태")
			// 				),
			// 				responseFields(
			// 						fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
			// 						fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL"),
			// 						fieldWithPath("time").type(JsonFieldType.STRING).description("예외 발생 시간"),
			// 						fieldWithPath("inputErrors").type(JsonFieldType.NULL).description("검증 실패 에러 정보")
			// 				)))
			// 		.andReturn();
		}

	}

	private EnrollmentListSummaryWithImageResponse getEnrollmentResponse(
			MarketEnrollment enrollment, Image image
	) {
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