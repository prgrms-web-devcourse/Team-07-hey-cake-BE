package com.programmers.heycake.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.mock.web.MockMultipartFile;

import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.model.entity.MarketEnrollment;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;
import com.programmers.heycake.domain.market.model.vo.MarketAddress;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.model.vo.MemberAuthority;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.order.model.entity.CakeInfo;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.vo.BreadFlavor;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
import com.programmers.heycake.domain.order.model.vo.CakeHeight;
import com.programmers.heycake.domain.order.model.vo.CakeSize;
import com.programmers.heycake.domain.order.model.vo.CreamFlavor;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;

public class TestUtil {

	public static Member getMember() {
		return new Member("John", "John123@naver.com", MemberAuthority.USER, "0311");
	}

	public static Member getMember(String nickname, String email) {
		return new Member(nickname, email, MemberAuthority.USER, "0311");
	}

	public static Market getMarket() {
		return Market.builder()
				.phoneNumber("01012345678")
				.marketAddress(new MarketAddress("서울", "성동구", "응봉동"))
				.openTime(LocalTime.now())
				.endTime(LocalTime.now())
				.description("업장 설명")
				.build();
	}

	public static MarketEnrollment getMarketEnrollment() {
		return MarketEnrollment.builder()
				.businessNumber("0123456789")
				.ownerName("Owner.Kong")
				.openDate(LocalDate.now())
				.marketName("서울 제과점")
				.phoneNumber("01012345678")
				.marketAddress(new MarketAddress("서울", "성동구", "응봉동"))
				.openTime(LocalTime.now())
				.endTime(LocalTime.now())
				.description("업장 설명")
				.enrollmentStatus(EnrollmentStatus.APPROVED)
				.build();
	}

	public static Order getOrder() {
		CakeInfo cakeInfo = CakeInfo.builder()
				.cakeCategory(CakeCategory.LETTERING)
				.cakeSize(CakeSize.SIZE_ONE)
				.cakeHeight(CakeHeight.ETC)
				.breadFlavor(BreadFlavor.CHOCO)
				.creamFlavor(CreamFlavor.CHOCO)
				.requirements("맛있게 해주세요")
				.build();

		return Order.builder()
				.memberId(1L)
				.title("초코 케이크 만들어주세요")
				.orderStatus(OrderStatus.NEW)
				.hopePrice(10000)
				.region("지역")
				.visitDate(LocalDateTime.now().plusDays(3))
				.cakeInfo(cakeInfo)
				.build();
	}

	public static Order getOrder(Long memberId, OrderStatus orderStatus) {
		CakeInfo cakeInfo = CakeInfo.builder()
				.cakeCategory(CakeCategory.LETTERING)
				.cakeSize(CakeSize.SIZE_ONE)
				.cakeHeight(CakeHeight.ETC)
				.breadFlavor(BreadFlavor.CHOCO)
				.creamFlavor(CreamFlavor.CHOCO)
				.requirements("맛있게 해주세요")
				.build();

		return Order.builder()
				.memberId(memberId)
				.title("초코 케이크 만들어주세요")
				.orderStatus(orderStatus)
				.hopePrice(10000)
				.region("지역")
				.visitDate(LocalDateTime.now().plusDays(3))
				.cakeInfo(cakeInfo)
				.build();
	}

	public static Order getOrder(Long memberId, OrderStatus orderStatus, LocalDateTime visitDate) {
		CakeInfo cakeInfo = CakeInfo.builder()
				.cakeCategory(CakeCategory.LETTERING)
				.cakeSize(CakeSize.SIZE_ONE)
				.cakeHeight(CakeHeight.ETC)
				.breadFlavor(BreadFlavor.CHOCO)
				.creamFlavor(CreamFlavor.CHOCO)
				.requirements("맛있게 해주세요")
				.build();

		return Order.builder()
				.memberId(memberId)
				.title("초코 케이크 만들어주세요")
				.orderStatus(orderStatus)
				.hopePrice(10000)
				.region("지역")
				.visitDate(visitDate)
				.cakeInfo(cakeInfo)
				.build();
	}

	public static Offer getOffer(Long marketId, int expectedPrice, String content) {
		return new Offer(marketId, expectedPrice, content);
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
}
