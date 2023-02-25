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
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class ImageIntegrationServiceTest {

	@Mock
	private ImageUploadService imageUploadService;

	@Mock
	private ImageService imageService;

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
		String savedUrl = subPath + "/" + UUID.randomUUID().toString() + ".jpg";
		Long referenceId = 1L;
		when(imageUploadService.upload(mockMultipartFile, subPath)).thenReturn(savedUrl);
		doNothing().when(imageService).createImage(referenceId, MARKET, savedUrl);

		// when
		imageIntegrationService.createAndUploadImage(mockMultipartFile, subPath, referenceId, MARKET);

		// then
		verify(imageUploadService).upload(mockMultipartFile, subPath);
		verify(imageService).createImage(referenceId, MARKET, savedUrl);
	}

	@Test
	@DisplayName("Success - DB 데이터 및 업로드된 이미지 삭제에 성공한다 - deleteImage")
	void deleteImageSuccess() {
		// given
		Long referenceId = 1L;
		String savedFilename = UUID.randomUUID().toString() + ".jpg";
		doNothing().when(imageService).deleteImage(referenceId, MARKET);
		doNothing().when(imageUploadService).delete(subPath, savedFilename);

		// when
		imageIntegrationService.deleteImage(referenceId, MARKET, subPath, savedFilename);

		// then
		verify(imageService).deleteImage(referenceId, MARKET);
		verify(imageUploadService).delete(subPath, savedFilename);
	}
}