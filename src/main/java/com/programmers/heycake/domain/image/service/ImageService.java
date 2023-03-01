package com.programmers.heycake.domain.image.service;

import static com.programmers.heycake.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.domain.image.mapper.ImageMapper;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.repository.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

	private final ImageRepository imageRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

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
		Image image = imageRepository.findAllByReferenceIdAndImageType(referenceId, imageType)
				.stream()
				.findFirst()
				.orElseThrow(() -> {
					throw new BusinessException(ENTITY_NOT_FOUND);
				});
		imageRepository.delete(image);
	}

	@Transactional
	public void createImages(List<Image> images) {
		images.stream()
				.forEach(this::createImage);
	}

	@Transactional(readOnly = true)
	public ImageResponses getImages(Long referenceId, ImageType imageType) {
		List<Image> images = imageRepository.findAllByReferenceIdAndImageType(referenceId, imageType);
		return ImageMapper.toResponse(images);
	}
}
