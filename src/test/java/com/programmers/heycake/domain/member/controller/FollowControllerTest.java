package com.programmers.heycake.domain.member.controller;

import static com.programmers.heycake.common.util.ApiDocumentUtils.*;
import static com.programmers.heycake.common.util.TestUtils.*;
import static com.programmers.heycake.domain.image.model.vo.ImageType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.repository.ImageRepository;
import com.programmers.heycake.domain.market.model.entity.Follow;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.repository.FollowRepository;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.model.vo.MemberAuthority;
import com.programmers.heycake.domain.member.repository.MemberRepository;

@Transactional
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class FollowControllerTest {
	private static final String ACCESS_TOKEN = "access_token";
	private static final String INVALID_ACCESS_TOKEN = "access_token";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MarketEnrollmentRepository marketEnrollmentRepository;

	@Autowired
	private MarketRepository marketRepository;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Nested
	@DisplayName("createFollow")
	@Transactional
	class CreateFollow {
		@Test
		@DisplayName("Success - follow 를 생성한다.")
		void createFollowSuccess() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.USER);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isCreated())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 생성 성공",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
							),
							responseHeaders(
									headerWithName("location").description("저장 url")
							)
					));
		}

		@Test
		@DisplayName("Fail - follow 생성 실패. (NotFound) - not exists market id")
		void createFollowBadRequest() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.USER);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId() + 1)
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isNotFound())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 생성 실패(NotFound)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
							),
							responseFields(
									fieldWithPath("message").description("에러 메세지"),
									fieldWithPath("path").description("에러 발생 uri"),
									fieldWithPath("time").description("에러 발생 시각"),
									fieldWithPath("inputErrors").description("에러 상세")
							)
					));
		}

		@Test
		@DisplayName("Fail - follow 생성 실패. (Unauthorized) - Unauthorized")
		void createFollowUnauthorized() throws Exception {
			//given
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", INVALID_ACCESS_TOKEN)
					).andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 생성 실패(Unauthorized)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
							),
							responseFields(
									fieldWithPath("message").description("에러 메세지"),
									fieldWithPath("path").description("에러 발생 uri"),
									fieldWithPath("time").description("에러 발생 시각"),
									fieldWithPath("inputErrors").description("에러 상세")
							)
					));
		}

		@Test
		@DisplayName("Fail - follow 생성 실패. (Forbidden) - role is not USER")
		void createFollowForbidden() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.MARKET);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 생성 실패(Forbidden)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
							),
							responseFields(
									fieldWithPath("message").description("에러 메세지"),
									fieldWithPath("path").description("에러 발생 uri"),
									fieldWithPath("time").description("에러 발생 시각"),
									fieldWithPath("inputErrors").description("에러 상세")
							)
					));
		}

		@Test
		@DisplayName("Fail - follow 생성 실패. (Conflict) 이미 follow된 경우")
		void createFollowConflictAlreadyFollow() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.USER);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.USER);

			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId())
					.header("access_token", ACCESS_TOKEN));

			//when // then
			mockMvc.perform(post("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isConflict())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 생성 실패(Conflict)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
							),
							responseFields(
									fieldWithPath("message").description("에러 메세지"),
									fieldWithPath("path").description("에러 발생 uri"),
									fieldWithPath("time").description("에러 발생 시각"),
									fieldWithPath("inputErrors").description("에러 상세")
							)
					));
		}

	}

	@Nested
	@DisplayName("deleteFollow")
	@Transactional
	class DeleteFollow {
		@Test
		@DisplayName("Success - follow 를 삭제한다.")
		void deleteFollowSuccess() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.USER);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			Follow follow = Follow.builder()
					.memberId(member.getId())
					.marketId(market.getId())
					.build();
			followRepository.save(follow);

			//when // then
			mockMvc.perform(delete("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isNoContent())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 삭제 성공",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
							)
					));
		}

		@Test
		@DisplayName("Fail - follow 를 삭제 실패.(NotFound)")
		void deleteFollowBadRequestNotExistsMarketId() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.USER);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			Follow follow = Follow.builder()
					.memberId(member.getId())
					.marketId(market.getId())
					.build();
			followRepository.save(follow);

			//when // then
			mockMvc.perform(delete("/api/v1/follows/{marketId}", market.getId() + 1)
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isNotFound())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 삭제 실패(NotFound) - 존재하지 않는 market_id",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
							),
							responseFields(
									fieldWithPath("message").description("에러 메세지"),
									fieldWithPath("path").description("에러 발생 uri"),
									fieldWithPath("time").description("에러 발생 시각"),
									fieldWithPath("inputErrors").description("에러 상세")
							)
					));
		}

		@Test
		@DisplayName("Fail - follow 를 삭제 실패.(BadRequest)")
		void deleteFollowBadRequestNotFollowed() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.USER);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(delete("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isBadRequest())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 삭제 실패(BadRequest) - 팔로우 하지 않은 market",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
							),
							responseFields(
									fieldWithPath("message").description("에러 메세지"),
									fieldWithPath("path").description("에러 발생 uri"),
									fieldWithPath("time").description("에러 발생 시각"),
									fieldWithPath("inputErrors").description("에러 상세")
							)
					));
		}

		@Test
		@DisplayName("Fail - follow 를 삭제 실패.(Unauthorized) Unauthorized")
		void deleteFollowUnauthorized() throws Exception {
			//given
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(delete("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", INVALID_ACCESS_TOKEN)
					).andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 삭제 실패(Unauthorized)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
							),
							responseFields(
									fieldWithPath("message").description("에러 메세지"),
									fieldWithPath("path").description("에러 발생 uri"),
									fieldWithPath("time").description("에러 발생 시각"),
									fieldWithPath("inputErrors").description("에러 상세")
							)
					));
		}

		@Test
		@DisplayName("Fail - follow 를 삭제 실패.(Forbidden)")
		void deleteFollowForbidden() throws Exception {
			//given
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(memberForMarket.getId(), MemberAuthority.MARKET);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			//when // then
			mockMvc.perform(delete("/api/v1/follows/{marketId}", market.getId())
							.header("access_token", ACCESS_TOKEN)
					).andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document(
							"follow/팔로우 삭제 실패(Forbidden)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							pathParameters(
									parameterWithName("marketId").description("마켓 식별자")
							),
							responseFields(
									fieldWithPath("message").description("에러 메세지"),
									fieldWithPath("path").description("에러 발생 uri"),
									fieldWithPath("time").description("에러 발생 시각"),
									fieldWithPath("inputErrors").description("에러 상세")
							)
					));
		}

	}

	@Nested
	@DisplayName("getFollowMarkets")
	@Transactional
	class GetFollowMarkets {

		@Test
		@DisplayName("Success - getFollowMarkets 조회 성공")
		void getFollowMarketsSuccess() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));
			setContext(member.getId(), MemberAuthority.USER);

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			Image image1 = new Image(market.getId(), MARKET, "imageUrl");
			Image image2 = new Image(market.getId(), MARKET, "imageUrl");
			Image image3 = new Image(market.getId(), MARKET, "imageUrl");
			imageRepository.saveAll(List.of(image1, image2, image3));

			followRepository.save(getFollow(member.getId(), market.getId()));

			//when //then
			mockMvc.perform(get("/api/v1/follows/my?cursorId=1&pageSize=10")
							.header("access_token", ACCESS_TOKEN)
					)
					.andExpect(status().isOk())
					.andDo(print())
					.andDo(document(
							"follow/내 팔로우 목록 조회 성공",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							requestParameters(
									parameterWithName("cursorId").description("커서 주문 식별자"),
									parameterWithName("pageSize").description("페이지 크기")
							),
							responseFields(
									fieldWithPath("myFollowMarkets").description("팔로우 목록"),
									fieldWithPath("myFollowMarkets[].id").description("업체 식별자"),
									fieldWithPath("myFollowMarkets[].phoneNumber").description("업체 전화번호"),
									fieldWithPath("myFollowMarkets[].address").description("업체 주소"),
									fieldWithPath("myFollowMarkets[].address.city").description("주소 시"),
									fieldWithPath("myFollowMarkets[].address.district").description("주소 구"),
									fieldWithPath("myFollowMarkets[].address.detailAddress").description("상세 주소"),
									fieldWithPath("myFollowMarkets[].openTime").description("오픈 시간"),
									fieldWithPath("myFollowMarkets[].endTime").description("마감 시간"),
									fieldWithPath("myFollowMarkets[].description").description("소개글"),
									fieldWithPath("myFollowMarkets[].marketName").description("상호명"),
									fieldWithPath("myFollowMarkets[].businessNumber").description("사업자 등록 번호"),
									fieldWithPath("myFollowMarkets[].ownerName").description("대표자 이름"),
									fieldWithPath("myFollowMarkets[].marketImage").description("업체 사진 url"),
									fieldWithPath("myFollowMarkets[].followerNumber").description("팔로우 수"),
									fieldWithPath("myFollowMarkets[].isFollowed").description("팔로우 유무"),
									fieldWithPath("cursorId").description("커서 식별자")
							)));
		}

		@Test
		@DisplayName("Fail - getFollowMarkets 조회 실패(Unauthorized)")
		void getFollowMarketsUnauthorized() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);

			Image image1 = new Image(market.getId(), MARKET, "imageUrl");
			Image image2 = new Image(market.getId(), MARKET, "imageUrl");
			Image image3 = new Image(market.getId(), MARKET, "imageUrl");
			imageRepository.saveAll(List.of(image1, image2, image3));

			followRepository.save(getFollow(member.getId(), market.getId()));

			//when //then
			mockMvc.perform(get("/api/v1/follows/my?cursorId=1&pageSize=10")
							.header("access_token", ACCESS_TOKEN)
					)
					.andExpect(status().isUnauthorized())
					.andDo(print())
					.andDo(document(
							"follow/내 팔로우 목록 조회 실패(Unauthorized)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							requestParameters(
									parameterWithName("cursorId").description("커서 주문 식별자"),
									parameterWithName("pageSize").description("페이지 크기")
							),
							responseFields(
									fieldWithPath("message").description("에러 메세지"),
									fieldWithPath("path").description("에러 발생 uri"),
									fieldWithPath("time").description("에러 발생 시각"),
									fieldWithPath("inputErrors").description("에러 상세")
							)
					));
		}

		@Test
		@DisplayName("Fail - getFollowMarkets 조회 실패(Forbidden)")
		void getFollowMarketsForbidden() throws Exception {
			//given
			Member member = memberRepository.save(getMember("test1@test.com"));
			Member memberForMarket = memberRepository.save(getMember("test2@test.com"));

			MarketEnrollment marketEnrollment =
					marketEnrollmentRepository.save(getMarketEnrollment("1231231111", memberForMarket));
			Market market = marketRepository.save(getMarket(memberForMarket, marketEnrollment));
			memberForMarket.changeAuthority(MemberAuthority.MARKET);
			setContext(memberForMarket.getId(), MemberAuthority.MARKET);

			Image image1 = new Image(market.getId(), MARKET, "imageUrl");
			Image image2 = new Image(market.getId(), MARKET, "imageUrl");
			Image image3 = new Image(market.getId(), MARKET, "imageUrl");
			imageRepository.saveAll(List.of(image1, image2, image3));

			followRepository.save(getFollow(member.getId(), market.getId()));

			//when //then
			mockMvc.perform(get("/api/v1/follows/my?cursorId=1&pageSize=10")
							.header("access_token", ACCESS_TOKEN)
					)
					.andExpect(status().isForbidden())
					.andDo(print())
					.andDo(document(
							"follow/내 팔로우 목록 조회 실패(Forbidden)",
							getDocumentRequest(),
							getDocumentResponse(),
							requestHeaders(
									headerWithName("access_token").description("인가 토큰")
							),
							requestParameters(
									parameterWithName("cursorId").description("커서 주문 식별자"),
									parameterWithName("pageSize").description("페이지 크기")
							),
							responseFields(
									fieldWithPath("message").description("에러 메세지"),
									fieldWithPath("path").description("에러 발생 uri"),
									fieldWithPath("time").description("에러 발생 시각"),
									fieldWithPath("inputErrors").description("에러 상세")
							)
					));
		}

	}

}
