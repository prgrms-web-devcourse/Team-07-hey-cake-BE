package com.programmers.heycake.domain.image.mapper;

import static java.util.stream.Collectors.*;

import java.util.List;

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.entity.Image;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageMapper {

	public static ImageResponses toResponse(List<Image> images) {
		List<ImageResponse> imageUrls = images
				.stream()
				.map(image -> new ImageResponse(image.getImageUrl(), image.getFilename()))
				.collect(toList());
		return new ImageResponses(imageUrls);
	}
}
