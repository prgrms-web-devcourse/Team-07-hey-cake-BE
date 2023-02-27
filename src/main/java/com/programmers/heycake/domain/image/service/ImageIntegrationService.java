package com.programmers.heycake.domain.image.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.image.event.DeleteEvent;
import com.programmers.heycake.domain.image.event.UploadRollbackEvent;
import com.programmers.heycake.domain.image.model.entity.Image;
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
		Image image = new Image(referenceId, imageType, savedUrl);
		applicationEventPublisher.publishEvent(new UploadRollbackEvent(subPath, image.getFilename()));
		imageService.createImage(image);
	}

	@Transactional
	public void deleteImage(Long referenceId, ImageType imageType, String subPath, String savedFilename) {
		imageService.deleteImage(referenceId, imageType);
		applicationEventPublisher.publishEvent(new DeleteEvent(subPath, savedFilename));
	}

}