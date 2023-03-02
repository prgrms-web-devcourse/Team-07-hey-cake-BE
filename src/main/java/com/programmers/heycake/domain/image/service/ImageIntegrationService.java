package com.programmers.heycake.domain.image.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.image.event.DeleteEvent;
import com.programmers.heycake.domain.image.event.UploadRollbackEvent;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.vo.ImageType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageIntegrationService {

	private final ImageUploadService imageUploadService;
	private final ImageService imageService;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public void createAndUploadImage(MultipartFile multipartFile, String subPath, Long referenceId, ImageType imageType) {
		if (multipartFile.isEmpty()) {
			throw new BusinessException(ErrorCode.BAD_REQUEST);
		}
		String savedUrl = imageUploadService.upload(multipartFile, subPath);
		applicationEventPublisher.publishEvent(new UploadRollbackEvent(subPath, getImageFilename(savedUrl)));
		imageService.createImage(referenceId, imageType, savedUrl);
	}

	@Transactional
	public void deleteImage(Long referenceId, ImageType imageType, String subPath) {
		List<String> imageUrls = imageService.deleteImage(referenceId, imageType);
		imageUrls
				.forEach(imageUrl -> {
					applicationEventPublisher.publishEvent(new DeleteEvent(subPath, getImageFilename(imageUrl)));
				});
	}

	@Transactional(readOnly = true)
	public ImageResponses getImages(Long referenceId, ImageType imageType) {
		return imageService.getImages(referenceId, imageType);
	}

	private String getImageFilename(String imageUrl) {
		int beforeFilenameIndex = imageUrl.lastIndexOf("/");
		return imageUrl.substring(beforeFilenameIndex + 1);
	}
}