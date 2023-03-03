// package com.programmers.heycake.domain.order.facade;
//
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import com.programmers.heycake.domain.offer.model.entity.Offer;
// import com.programmers.heycake.domain.offer.service.OfferService;
// import com.programmers.heycake.domain.order.model.vo.request.HistoryControllerRequest;
// import com.programmers.heycake.domain.order.model.vo.request.HistoryRequest;
// import com.programmers.heycake.domain.order.service.HistoryService;
//
// @ExtendWith(MockitoExtension.class)
// public class HistoryFacadeTest {
// 	@InjectMocks
// 	private HistoryFacade historyFacade;
//
// 	@Mock
// 	private HistoryService historyService;
//
// 	@Mock
// 	private OfferService offerService;
//
// 	@Nested
// 	@DisplayName("createHistory")
// 	class CreateHistoryTest {
// 		@Test
// 		@DisplayName("Success - orderHistory 를 생성한다. - createHistory")
// 		void createHistorySuccess() {
// 			//given
// 			HistoryControllerRequest historyControllerRequest = new HistoryControllerRequest(1L, 1L);
// 			Offer offer = new Offer(1L, 100, "content");
//
// 			//when
// 			when(offerService.findById(anyLong()))
// 					.thenReturn(offer);
// 			historyFacade.createHistory(historyControllerRequest);
//
// 			//then
// 			verify(offerService).findById(anyLong());
// 			verify(historyService).createHistory(any(HistoryRequest.class));
// 		}
// 	}
// }
