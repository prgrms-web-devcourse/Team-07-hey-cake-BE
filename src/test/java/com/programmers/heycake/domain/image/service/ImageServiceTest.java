package com.programmers.heycake.domain.image.service;

import static org.mockito.Mockito.*;

import java.util.Optional;

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
import com.programmers.heycake.domain.image.model.vo.ImageType;
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
		imageService.createImage(referenceId, ImageType.MARKET, savedUrl);

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
			String savedUrl = "savedUrl";
			Image image = new Image(referenceId, ImageType.MARKET, savedUrl);
			when(imageRepository.findByReferenceIdAndImageType(referenceId, ImageType.MARKET))
					.thenReturn(Optional.of(image));
			doNothing().when(imageRepository).delete(image);

			// when
			imageService.deleteImage(referenceId, ImageType.MARKET);

			// then
			verify(imageRepository).findByReferenceIdAndImageType(referenceId, ImageType.MARKET);
			verify(imageRepository).delete(image);
		}

		@Test
		@DisplayName("Fail - 존재하지 않는 이미지의 경우 삭제에 실패한다")
		void deleteImageFailByNotFound() {
			// given
			Long referenceId = 1L;
			when(imageRepository.findByReferenceIdAndImageType(referenceId, ImageType.MARKET))
					.thenReturn(Optional.empty());

			// when & then
			Assertions.assertThatThrownBy(() -> imageService.deleteImage(referenceId, ImageType.MARKET))
					.isExactlyInstanceOf(BusinessException.class)
					.hasFieldOrPropertyWithValue("errorCode", ErrorCode.ENTITY_NOT_FOUND);
		}
	}
}