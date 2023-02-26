package com.programmers.heycake.domain.image.model.entity;

import static com.programmers.heycake.domain.image.model.vo.ImageType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ImageTest {

	@Test
	@DisplayName("Success - 저장된 파일명 조회에 성공한다 - getFilename")
	void getFilenameSuccess() {
		// given
		String filename = UUID.randomUUID().toString() + ".jpg";
		String savedUrl = "file://kwon/" + filename;
		Image image = new Image(1L, MARKET, savedUrl);

		// when
		String findFilename = image.getFilename();

		// then
		assertThat(findFilename).isEqualTo(filename);
	}
}