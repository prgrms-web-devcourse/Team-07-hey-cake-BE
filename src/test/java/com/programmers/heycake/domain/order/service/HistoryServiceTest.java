package com.programmers.heycake.domain.order.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;
import com.programmers.heycake.domain.order.model.vo.request.HistoryRequest;
import com.programmers.heycake.domain.order.repository.HistoryRepository;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {
	@InjectMocks
	HistoryService historyService;
	@Mock
	HistoryRepository historyRepository;

	// @Nested
	// @DisplayName("createHistory")
	// class CreateHistoryTest {
	// 	@Test
	// 	@DisplayName("Success - orderHistory 를 생성한다. - createHistory")
	// 	public void createHistorySuccess() {
	// 		//given
	// 		HistoryRequest historyRequest = new HistoryRequest(1L, 1L, Order.builder().build());
	//
	// 		//when
	// 		when(historyRepository.save(any(OrderHistory.class)))
	// 				.thenReturn(new OrderHistory(1L, 1L));
	// 		historyService.createHistory(historyRequest);
	//
	// 		//then
	// 		verify(historyRepository).save(any(OrderHistory.class));
	// 	}
	// }

}
