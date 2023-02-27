package com.programmers.heycake.domain.image.mapper;

import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.entity.Image;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageMapper {

	public static ImageResponse toResponse(Image image) {
		return new ImageResponse(image.getImageUrl());
	}
}
