package com.programmers.heycake.domain.image.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.image.event.RollbackUploadEvent;
import com.programmers.heycake.domain.image.mapper.ImageMapper;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.repository.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

	private final ImageStorageService imageStorageService;
	private final ImageRepository imageRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public void createAndUploadImage(MultipartFile multipartFile, String subPath, Long referenceId, ImageType imageType) {
		if (multipartFile.isEmpty()) {
			throw new BusinessException(ErrorCode.BAD_REQUEST);
		}
		String savedUrl = imageStorageService.upload(multipartFile, subPath);
		applicationEventPublisher.publishEvent(new RollbackUploadEvent(subPath, getImageFilename(savedUrl)));
		imageRepository.save(new Image(referenceId, imageType, savedUrl));
	}

	@Transactional
	public void deleteImages(Long referenceId, ImageType imageType) {
		imageRepository.softDeleteByReferenceIdAndImageType(referenceId, imageType);
	}

	@Transactional(readOnly = true)
	public ImageResponses getImages(Long referenceId, ImageType imageType) {
		List<Image> images = imageRepository.findAllByReferenceIdAndImageType(referenceId, imageType);
		return ImageMapper.toResponse(images);
	}

	@Transactional(readOnly = true)
	public String getImageUrl(Long referenceId, ImageType imageType) {
		List<Image> images = imageRepository.findAllByReferenceIdAndImageType(referenceId, imageType);

		return images.stream()
				.findFirst()
				.map(Image::getImageUrl)
				.orElse(null);
	}

	private String getImageFilename(String imageUrl) {
		int beforeFilenameIndex = imageUrl.lastIndexOf("/");
		return imageUrl.substring(beforeFilenameIndex + 1);
	}
}
