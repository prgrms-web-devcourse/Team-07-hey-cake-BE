package com.programmers.heycake.domain.image.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.repository.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

	private final ImageRepository imageRepository;

	@Transactional
	public void createImage(Long referenceId, ImageType imageType, String savedUrl) {
		imageRepository.save(new Image(referenceId, imageType, savedUrl));
	}

	@Transactional
	public void createImage(Image image) {
		imageRepository.save(image);
	}

	@Transactional
	public void deleteImage(Long referenceId, ImageType imageType) {
		imageRepository.findByReferenceIdAndImageType(referenceId, imageType)
				.ifPresentOrElse(
						imageRepository::delete,
						() -> {
							throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
						}
				);
	}

	@Transactional
	public void createImages(List<Image> images) {
		images.stream()
				.forEach(this::createImage);
	}
}
