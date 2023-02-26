package com.programmers.heycake.domain.order.model.entity;

import static com.programmers.heycake.utils.TestUtil.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import com.programmers.heycake.domain.order.model.vo.OrderStatus;

class OrderTest {

	@CsvSource({"2023-05-31T00:00:01", "2024-05-31T00:00:00"})
	@DisplayName("Success - 픽업 시간보다 지난 시간이면 true 를 반환한다.. - isPassedVisitDate")
	@ParameterizedTest
	void isPassedVisitDateTrue(LocalDateTime targetDate) {
		// given
		LocalDateTime visitDate = LocalDateTime.of(2023, 5, 31, 0, 0);
		Order order = getOrder(1L, OrderStatus.NEW, visitDate);

		// when
		boolean result = order.isPassedVisitDate(targetDate);

		// then
		assertThat(result).isTrue();
	}

	@CsvSource({"2023-05-31T00:00:00", "2023-05-30T23:59:59", "2022-05-30T00:00:00"})
	@DisplayName("Success - 픽업 시간과 같은 시간이거나 이전 시간이면 false 를 반환한다. - isPassedVisitDate")
	@ParameterizedTest
	void isPassedVisitDateFalse(LocalDateTime targetDate) {
		// given
		LocalDateTime visitDate = LocalDateTime.of(2023, 5, 31, 0, 0);
		Order order = getOrder(1L, OrderStatus.NEW, visitDate);

		// when
		boolean result = order.isPassedVisitDate(targetDate);

		// then
		assertThat(result).isFalse();
	}

	@EnumSource(value = OrderStatus.class, names = {"RESERVED", "PAID"})
	@DisplayName("Success - OrderStatus 가 RESERVED, PAID 면 true 를 반환한다. - isClosed")
	@ParameterizedTest
	void isClosedTrue(OrderStatus orderStatus) {
		// given
		Order order = getOrder(1L, orderStatus);

		// when
		boolean result = order.isClosed();

		// then
		assertThat(result).isTrue();
	}

	@EnumSource(value = OrderStatus.class, names = {"NEW"})
	@DisplayName("Success - OrderStatus 가 NEW 면 false 를 반환한다. - isClosed")
	@ParameterizedTest
	void isClosedFalse(OrderStatus orderStatus) {
		// given
		Order order = getOrder(1L, orderStatus);

		// when
		boolean result = order.isClosed();

		// then
		assertThat(result).isFalse();
	}
}