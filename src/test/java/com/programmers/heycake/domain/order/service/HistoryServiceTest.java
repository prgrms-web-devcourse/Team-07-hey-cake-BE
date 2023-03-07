package com.programmers.heycake.domain.order.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.programmers.heycake.domain.order.model.dto.request.HistoryFacadeRequest;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.entity.OrderHistory;
import com.programmers.heycake.domain.order.repository.HistoryRepository;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {
	@InjectMocks
	HistoryService historyService;
	@Mock
	HistoryRepository historyRepository;

	@Nested
	@DisplayName("createHistory")
	class CreateHistoryTest {
		@Test
		@DisplayName("Success - orderHistory 를 생성한다.")
		public void createHistorySuccess() {
			//given
			Order order = Order.builder().build();
			HistoryFacadeRequest historyFacadeRequest = new HistoryFacadeRequest(1L, 1L, order);

			//when
			when(historyRepository.save(any(OrderHistory.class)))
					.thenReturn(new OrderHistory(1L, 1L));
			historyService.createHistory(historyFacadeRequest);

			//then
			verify(historyRepository).save(any(OrderHistory.class));
		}
	}

}
