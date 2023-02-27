package com.programmers.heycake.domain.offer.facade;

import static com.programmers.heycake.utils.TestUtil.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageIntegrationService;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.offer.model.dto.request.OfferSaveRequest;
import com.programmers.heycake.domain.offer.service.OfferService;

@ExtendWith(MockitoExtension.class)
class OfferFacadeTest {
	@InjectMocks
	OfferFacade offerFacade;

	@Mock
	OfferService offerService;

	@Mock
	ImageIntegrationService imageIntegrationService;

	@Mock
	ImageService imageService;

	@Nested
	@DisplayName("deleteOffer")
	class DeleteOffer {

		@Test
		@DisplayName("Success - offer와 관련된 정보를 삭제한다.")
		void deleteOfferSuccess() {
			//given
			ImageResponse url = new ImageResponse("url");

			//when
			doReturn(url).when(imageService).getImage(anyLong(), any(ImageType.class));
			offerFacade.deleteOffer(1L);

			//then
			//TODO 호출되는 메서드 추가되는대로 추가하기
			verify(offerService).deleteOffer(anyLong());
			verify(imageIntegrationService).deleteImage(anyLong(), any(ImageType.class), anyString(), anyString());
		}
	}

	@Nested
	@DisplayName("saveOffer")
	class SaveOffer {

		@Test
		@DisplayName("Success - 제안 생성에 성공한다. - saveOffer")
		void saveOfferSuccess() {
			// given
			OfferSaveRequest request = new OfferSaveRequest(1L, 50000, "내용", getMockFile());
			Long savedOfferId = 1L;

			when(offerService.saveOffer(any(Long.class), eq(1L), eq(request.expectedPrice()), eq(request.content())))
					.thenReturn(savedOfferId);
			doNothing().when(imageIntegrationService)
					.createAndUploadImage(any(), any(String.class), eq(savedOfferId), eq(ImageType.OFFER));

			// when
			offerFacade.saveOffer(request, 1L);

			// then
			verify(offerService).saveOffer(1L, request.orderId(), request.expectedPrice(), request.content());
			verify(imageIntegrationService).createAndUploadImage(eq(request.offerImage()), any(String.class),
					eq(savedOfferId), eq(ImageType.OFFER));
		}
	}
}
