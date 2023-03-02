package com.programmers.heycake.domain.image.service;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.repository.ImageRepository;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

	@Mock
	private ImageRepository imageRepository;

	@InjectMocks
	private ImageService imageService;

	@Test
	@DisplayName("Success - 이미지 생성, 저장에 성공한다 - createImage")
	void createImageSuccess() {
		// given
		Long referenceId = 1L;
		String savedUrl = "savedUrl";
		when(imageRepository.save(any(Image.class))).thenReturn(any(Image.class));

		// when
		imageService.createImage(referenceId, MARKET, savedUrl);

		// then
		verify(imageRepository).save(any(Image.class));
	}

	@Nested
	@DisplayName("deleteImage")
	class DeleteImage {

		@Test
		@DisplayName("Success - 이미지 삭제에 성공한다")
		void deleteImageSuccess() {
			// given
			Long referenceId = 1L;
			String savedUrl1 = "savedUrl1";
			String savedUrl2 = "savedUrl2";
			Image image1 = new Image(referenceId, ORDER, savedUrl1);
			Image image2 = new Image(referenceId, ORDER, savedUrl2);
			List<Image> images = List.of(image1, image2);
			when(imageRepository.findAllByReferenceIdAndImageType(referenceId, ORDER))
					.thenReturn(images);
			doNothing().when(imageRepository).delete(any(Image.class));

			// when
			imageService.deleteImage(referenceId, ORDER);

			// then
			verify(imageRepository).findAllByReferenceIdAndImageType(referenceId, ORDER);
			verify(imageRepository).delete(any(Image.class));
		}

		@Test
		@DisplayName("Fail - 존재하지 않는 이미지의 경우 삭제에 실패한다")
		void deleteImageFailByNotFound() {
			// given
			Long referenceId = 1L;
			when(imageRepository.findAllByReferenceIdAndImageType(referenceId, ORDER))
					.thenReturn(Collections.emptyList());

			// when & then
			Assertions.assertThatThrownBy(() -> imageService.deleteImage(referenceId, ORDER))
					.isExactlyInstanceOf(BusinessException.class)
					.hasFieldOrPropertyWithValue("errorCode", ErrorCode.ENTITY_NOT_FOUND);
		}
	}
}