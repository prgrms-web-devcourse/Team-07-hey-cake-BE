package com.programmers.heycake.domain.image.service;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;

import com.programmers.heycake.domain.image.event.DeleteEvent;
import com.programmers.heycake.domain.image.event.UploadRollbackEvent;
import com.programmers.heycake.domain.image.model.entity.Image;

@ExtendWith(MockitoExtension.class)
class ImageIntegrationServiceTest {

	@Mock
	private ImageUploadService imageUploadService;

	@Mock
	private ImageService imageService;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@InjectMocks
	private ImageIntegrationService imageIntegrationService;

	private String originalFilename = "test.jpg";
	private String contentType = "image/jpg";
	private String subPath = "test";
	private MockMultipartFile mockMultipartFile = new MockMultipartFile(
			"test",
			originalFilename,
			contentType,
			"test".getBytes()
	);

	@Test
	@DisplayName("Success - 이미지 저장 및 업로드에 성공한다 - createAndUploadImage")
	void createAndUploadImageSuccess() {
		// given
		Long referenceId = 1L;
		String savedUrl = subPath + "/" + UUID.randomUUID().toString() + ".jpg";
		when(imageUploadService.upload(mockMultipartFile, subPath)).thenReturn(savedUrl);
		doNothing().when(applicationEventPublisher).publishEvent(any(UploadRollbackEvent.class));
		doNothing().when(imageService).createImage(any(Image.class));

		// when
		imageIntegrationService.createAndUploadImage(mockMultipartFile, subPath, referenceId, MARKET);

		// then
		verify(imageUploadService).upload(mockMultipartFile, subPath);
		verify(applicationEventPublisher).publishEvent(any(UploadRollbackEvent.class));
		verify(imageService).createImage(any(Image.class));
	}

	@Test
	@DisplayName("Success - DB 데이터 및 업로드된 이미지 삭제에 성공한다 - deleteImage")
	void deleteImageSuccess() {
		// given
		Long referenceId = 1L;
		String savedFilename = UUID.randomUUID().toString() + ".jpg";
		doNothing().when(imageService).deleteImage(referenceId, MARKET);
		doNothing().when(applicationEventPublisher).publishEvent(any(DeleteEvent.class));

		// when
		imageIntegrationService.deleteImage(referenceId, MARKET, subPath, savedFilename);

		// then
		verify(imageService).deleteImage(referenceId, MARKET);
		verify(applicationEventPublisher).publishEvent(any(DeleteEvent.class));
	}
}