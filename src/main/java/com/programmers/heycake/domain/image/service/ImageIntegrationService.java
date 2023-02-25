package com.programmers.heycake.domain.image.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.programmers.heycake.domain.image.model.vo.ImageType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageIntegrationService {

	private final ImageUploadService imageUploadService;
	private final ImageService imageService;

	@Transactional
	public void createAndUploadImage(MultipartFile multipartFile, String subPath, Long referenceId, ImageType imageType) {
		String savedUrl = imageUploadService.upload(multipartFile, subPath);
		imageService.createImage(referenceId, imageType, savedUrl);
	}

	@Transactional
	public void deleteImage(Long referenceId, ImageType imageType, String subPath, String savedFilename) {
		imageService.deleteImage(referenceId, imageType);
		imageUploadService.delete(subPath, savedFilename);
	}

}
