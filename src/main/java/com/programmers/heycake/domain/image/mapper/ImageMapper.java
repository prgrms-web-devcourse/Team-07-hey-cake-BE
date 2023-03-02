package com.programmers.heycake.domain.image.mapper;

import java.util.List;

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.entity.Image;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageMapper {

	public static ImageResponses toResponse(List<Image> images) {
		List<ImageResponse> imageResponses = images
				.stream()
				.map(image -> new ImageResponse(image.getImageUrl(), image.getFilename()))
				.toList();
		return new ImageResponses(imageResponses);
	}
}
