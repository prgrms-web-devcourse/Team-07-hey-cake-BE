package com.programmers.heycake.domain.image.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.entity.Image;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageMapper {

	public static ImageResponse toResponse(List<Image> images) {
		List<String> imageUrls = images
				.stream()
				.map(image -> image.getImageUrl())
				.collect(Collectors.toList());

		return new ImageResponse(imageUrls);
	}
}
