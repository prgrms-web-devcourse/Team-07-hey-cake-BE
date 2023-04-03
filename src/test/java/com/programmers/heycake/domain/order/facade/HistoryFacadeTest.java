package com.programmers.heycake.domain.order.facade;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.programmers.heycake.common.util.AuthenticationUtil;
import com.programmers.heycake.common.util.WithMockCustomUser;
import com.programmers.heycake.domain.facade.HistoryFacade;
import com.programmers.heycake.domain.offer.model.dto.OfferDto;
import com.programmers.heycake.domain.offer.service.OfferService;
import com.programmers.heycake.domain.order.model.dto.OrderDto;
import com.programmers.heycake.domain.order.model.dto.request.HistoryCreateControllerRequest;
import com.programmers.heycake.domain.order.model.dto.request.HistoryCreateFacadeRequest;
import com.programmers.heycake.domain.order.model.entity.Order;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.programmers.heycake.domain.order.service.HistoryService;
import com.programmers.heycake.domain.order.service.OrderService;

@ExtendWith(MockitoExtension.class)
public class HistoryFacadeTest {
	@InjectMocks
	private HistoryFacade historyFacade;

	@Mock
	private HistoryService historyService;

	@Mock
	private OfferService offerService;

	@Mock
	private OrderService orderService;

	@Nested
	@DisplayName("createHistory")
	class CreateHistoryTest {
		@Test
		@DisplayName("Success - orderHistory 를 생성한다.")
		@WithMockCustomUser(memberId = 1L)
		void createHistorySuccess() {
			//given
			HistoryCreateControllerRequest historyControllerRequest = new HistoryCreateControllerRequest(1L, 1L, true);
			OrderDto orderDto = OrderDto.builder().id(1L).build();
			OfferDto offerDto = OfferDto.builder().orderDto(orderDto).build();

			//when
			try (MockedStatic<AuthenticationUtil> authenticationUtil = Mockito.mockStatic(AuthenticationUtil.class)) {
				given(AuthenticationUtil.getMemberId()).willReturn(1L);
				doNothing().when(orderService).updateOrderState(anyLong(), any(OrderStatus.class));
				doReturn(Order.builder().build()).when(orderService).getOrderById(anyLong());
				doReturn(offerDto).when(offerService).getOfferById(anyLong());
				doReturn(1L).when(historyService).createHistory(any(HistoryCreateFacadeRequest.class));
				historyFacade.createHistory(historyControllerRequest);
			}

			//then
			verify(orderService).updateOrderState(anyLong(), any(OrderStatus.class));
			verify(offerService).getOfferById(anyLong());
			verify(orderService).getOrderById(anyLong());
			verify(historyService).createHistory(any(HistoryCreateFacadeRequest.class));
		}
	}
}
