package com.programmers.heycake.domain.image.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.image.event.UploadRollbackEvent;
import com.programmers.heycake.domain.image.mapper.ImageMapper;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.repository.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

	private final ImageUploadService imageUploadService;
	private final ImageRepository imageRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public void createAndUploadImage(MultipartFile multipartFile, String subPath, Long referenceId, ImageType imageType) {
		if (multipartFile.isEmpty()) {
			throw new BusinessException(ErrorCode.BAD_REQUEST);
		}
		String savedUrl = imageUploadService.upload(multipartFile, subPath);
		applicationEventPublisher.publishEvent(new UploadRollbackEvent(subPath, getImageFilename(savedUrl)));
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

	private String getImageFilename(String imageUrl) {
		int beforeFilenameIndex = imageUrl.lastIndexOf("/");
		return imageUrl.substring(beforeFilenameIndex + 1);
	}
}