package com.programmers.heycake.domain.image.service;

import static com.programmers.heycake.domain.image.service.TestUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.image.repository.ImageRepository;
import com.programmers.heycake.domain.market.repository.MarketEnrollmentRepository;
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.offer.model.dto.request.OfferSaveRequest;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.programmers.heycake.domain.order.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class OfferControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MarketRepository marketRepository;

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private MarketEnrollmentRepository marketEnrollmentRepository;

	@Autowired
	private ImageRepository imageRepository;

	@MockBean
	private ImageUploadService imageUploadService;

	@Nested
	@DisplayName("saveOffer")
	@Transactional
	class SaveOffer {

		@Test
		@WithMockUser
		@DisplayName("Fail - 글 작성 회원이 업주가 아닌 경우 실패한다. - saveOffer")
		void saveOfferWriterIsNotMarketFail() throws Exception {
			// given
			Member writeOrderMember = getMember("writer", "writer@naver.com");
			Member normalMember = getMember("normal", "normal@naver.com");
			memberRepository.saveAll(List.of(writeOrderMember, normalMember));

			Order order = getOrder(writeOrderMember.getId(), OrderStatus.NEW);
			orderRepository.save(order);

			OfferSaveRequest request = new OfferSaveRequest(order.getId(), 50000, "내용", getMockFile(), normalMember.getId());
			String imageUrl = "imageURL";

			when(imageUploadService.upload(any(), any()))
					.thenReturn(imageUrl);

			// when
			mockMvc.perform(
							multipart("/api/v1/offers")
									.file("offerImage", request.offerImage().getBytes())
									.param("orderId", request.orderId().toString())
									.param("expectedPrice", String.valueOf(request.expectedPrice()))
									.param("content", request.content())
									.param("memberId", normalMember.getId().toString())
									.contentType(MediaType.MULTIPART_FORM_DATA)
									.with(csrf())
					)
					.andDo(print())
					.andExpect(status().isForbidden())
					.andExpect(jsonPath("message").value(ErrorCode.FORBIDDEN.getMessage()))
					.andExpect(jsonPath("path").value("/api/v1/offers"))
					.andExpect(jsonPath("time").exists())
					.andExpect(jsonPath("inputErrors").isEmpty())
					.andDo(document("offer/제안 생성 실패 - 글 작성 회원이 업주가 아닌 경우",
									preprocessRequest(prettyPrint()),
									preprocessResponse(prettyPrint()),
									requestParts(
											partWithName("offerImage").description("케이크 이미지")
									),
									requestParameters(
											parameterWithName("orderId").description("주문 id"),
											parameterWithName("expectedPrice").description("예상 가격"),
											parameterWithName("content").description("내용"),
											parameterWithName("memberId").description("글 작성자 id"),
											parameterWithName("_csrf").description("csrf token")
									),
									responseFields(
											fieldWithPath("message").description("실패 메세지"),
											fieldWithPath("path").description("실패 URL"),
											fieldWithPath("time").description("실패 시각"),
											fieldWithPath("inputErrors").description("입력값 검증 실패 리스트")
									)
							)
					);
		}
	}
}