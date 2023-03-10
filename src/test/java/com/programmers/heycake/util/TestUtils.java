package com.programmers.heycake.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.market.model.dto.request.EnrollmentCreateRequest;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.model.vo.MarketAddress;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.model.vo.MemberAuthority;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.order.model.entity.CakeInfo;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;
import com.programmers.heycake.domain.order.model.vo.BreadFlavor;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
import com.programmers.heycake.domain.order.model.vo.CakeHeight;
import com.programmers.heycake.domain.order.model.vo.CakeSize;
import com.programmers.heycake.domain.order.model.vo.CreamFlavor;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;

public class TestUtils {

	public static Member getMember() {
		return new Member("heycake@heycake.com", MemberAuthority.USER, "0311", "nickname");
	}

	public static Member getMember(String email) {
		return new Member(email, MemberAuthority.USER, "0311", "nickname");
	}

	public static Market getMarket() {
		return Market.builder()
				.phoneNumber("01012345678")
				.marketAddress(new MarketAddress("??????", "?????????", "?????????"))
				.openTime(LocalTime.now())
				.endTime(LocalTime.now())
				.description("?????? ??????")
				.build();
	}

	public static Market getMarket(String phoneNumber, Member member, MarketEnrollment marketEnrollment) {
		Market market = Market.builder()
				.phoneNumber("01012345678")
				.marketAddress(new MarketAddress("??????", "?????????", "?????????"))
				.openTime(LocalTime.now())
				.endTime(LocalTime.now())
				.description("?????? ??????")
				.build();
		market.setMember(member);
		market.setMarketEnrollment(marketEnrollment);

		return market;
	}

	public static MarketEnrollment getMarketEnrollment() {
		return MarketEnrollment.builder()
				.businessNumber("1234567890")
				.ownerName("Owner.Kong")
				.openDate(LocalDate.now())
				.marketName("?????? ?????????")
				.phoneNumber("01012345678")
				.marketAddress(new MarketAddress("??????", "?????????", "?????????"))
				.openTime(LocalTime.now())
				.endTime(LocalTime.now())
				.description("?????? ??????")
				.build();
	}

	public static MarketEnrollment getMarketEnrollment(String businessNumber) {
		return MarketEnrollment.builder()
				.businessNumber(businessNumber)
				.ownerName("Owner.Kong")
				.openDate(LocalDate.now())
				.marketName("?????? ?????????")
				.phoneNumber("01012345678")
				.marketAddress(new MarketAddress("??????", "?????????", "?????????"))
				.openTime(LocalTime.of(9, 0))
				.endTime(LocalTime.of(18, 0))
				.description("?????? ??????")
				.build();
	}

	public static MarketEnrollment getMarketEnrollment(String businessNumber, Member member) {
		MarketEnrollment marketEnrollment = MarketEnrollment.builder()
				.businessNumber(businessNumber)
				.ownerName("Owner.Kong")
				.openDate(LocalDate.now())
				.marketName("?????? ?????????")
				.phoneNumber("01012345678")
				.marketAddress(new MarketAddress("??????", "?????????", "?????????"))
				.openTime(LocalTime.now())
				.endTime(LocalTime.now())
				.description("?????? ??????")
				.build();
		marketEnrollment.setMember(member);

		return marketEnrollment;
	}

	public static Order getOrder(Long memberId) {
		CakeInfo cakeInfo = CakeInfo.builder()
				.cakeCategory(CakeCategory.LETTERING)
				.cakeSize(CakeSize.NO_1)
				.cakeHeight(CakeHeight.ETC)
				.breadFlavor(BreadFlavor.CHOCO)
				.creamFlavor(CreamFlavor.CHOCO)
				.requirements("????????? ????????????")
				.build();

		return Order.builder()
				.memberId(memberId)
				.title("?????? ????????? ??????????????????")
				.orderStatus(OrderStatus.NEW)
				.hopePrice(10000)
				.region("??????")
				.visitDate(LocalDateTime.now().plusDays(3))
				.cakeInfo(cakeInfo)
				.build();
	}

	public static Order getOrder(Long memberId, OrderStatus orderStatus) {
		CakeInfo cakeInfo = CakeInfo.builder()
				.cakeCategory(CakeCategory.LETTERING)
				.cakeSize(CakeSize.NO_1)
				.cakeHeight(CakeHeight.ETC)
				.breadFlavor(BreadFlavor.CHOCO)
				.creamFlavor(CreamFlavor.CHOCO)
				.requirements("????????? ????????????")
				.build();

		return Order.builder()
				.memberId(memberId)
				.title("?????? ????????? ??????????????????")
				.orderStatus(orderStatus)
				.hopePrice(10000)
				.region("??????")
				.visitDate(LocalDateTime.now().plusDays(3))
				.cakeInfo(cakeInfo)
				.build();
	}

	public static Order getOrder(Long memberId, OrderStatus orderStatus, LocalDateTime visitDate) {
		CakeInfo cakeInfo = CakeInfo.builder()
				.cakeCategory(CakeCategory.LETTERING)
				.cakeSize(CakeSize.NO_1)
				.cakeHeight(CakeHeight.ETC)
				.breadFlavor(BreadFlavor.CHOCO)
				.creamFlavor(CreamFlavor.CHOCO)
				.requirements("????????? ????????????")
				.build();

		return Order.builder()
				.memberId(memberId)
				.title("?????? ????????? ??????????????????")
				.orderStatus(orderStatus)
				.hopePrice(10000)
				.region("??????")
				.visitDate(visitDate)
				.cakeInfo(cakeInfo)
				.build();
	}

	public static Offer getOffer(Long marketId, int expectedPrice, String content) {
		return new Offer(marketId, expectedPrice, content);
	}

	public static Offer getOffer(Long marketId, int expectedPrice, String content, Order order) {
		Offer offer = new Offer(marketId, expectedPrice, content);
		offer.setOrder(order);

		return offer;
	}

	public static Comment getComment(Long memberId, Offer offer) {
		Comment comment = new Comment(memberId, "comment");
		comment.setOffer(offer);

		return comment;
	}

	public static OrderHistory getOrderHistory(Long memberId, Long marketId, Order order) {
		OrderHistory orderHistory = new OrderHistory(memberId, marketId);
		orderHistory.setOrder(order);

		return orderHistory;
	}

	public static Image getImage(Long referenceId, ImageType imageType, String imageUrl) {
		return new Image(referenceId, imageType, imageUrl);
	}

	public static MockMultipartFile getMockFile() {
		return new MockMultipartFile(
				"test",
				"test.jpg",
				"image/jpg",
				"test".getBytes()
		);
	}

	public static void setContext(Long memberId, MemberAuthority memberAuthority) {
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(
				new UsernamePasswordAuthenticationToken(memberId, null,
						List.of(new SimpleGrantedAuthority(memberAuthority.getRole()))));
	}

	public static EnrollmentCreateRequest getEnrollmentRequest(String businessNumber) {
		return EnrollmentCreateRequest.builder()
				.businessNumber(businessNumber)
				.ownerName("?????????")
				.openDate(LocalDate.of(1997, 10, 10))
				.marketName("????????????")
				.phoneNumber("01012345678")
				.city("???????????????")
				.district("?????????")
				.detailAddress("????????????")
				.openTime(LocalTime.of(9, 0))
				.endTime(LocalTime.of(18, 0))
				.description("??????'s ??????")
				.businessLicenseImage(getMockFile())
				.marketImage(getMockFile())
				.build();
	}

}
