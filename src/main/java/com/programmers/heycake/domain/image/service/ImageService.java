package com.programmers.heycake.domain.image.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public void createImage(Long referenceId, ImageType imageType, String savedUrl) {
		imageRepository.save(new Image(referenceId, imageType, savedUrl));
	}

	@Transactional
	public List<String> deleteImage(Long referenceId, ImageType imageType) {
		List<Image> images = imageRepository.findAllByReferenceIdAndImageType(referenceId, imageType);
		imageRepository.deleteAllByReferenceIdAndImageType(referenceId, imageType);

		return images.stream()
				.map(Image::getImageUrl)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public ImageResponses getImages(Long referenceId, ImageType imageType) {
		List<Image> images = imageRepository.findAllByReferenceIdAndImageType(referenceId, imageType);
		return ImageMapper.toResponse(images);
	}
}
