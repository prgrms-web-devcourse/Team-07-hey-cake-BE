package com.programmers.heycake.domain.image.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.programmers.heycake.common.config.S3MockConfig;

import io.findify.s3mock.S3Mock;

@Import(S3MockConfig.class)
@SpringBootTest
class ImageS3UploadServiceTest {

	private static final String BASE_URL = "http://localhost:8001";

	@Value("${cloud.aws.s3.bucket}")
	private String BUCKET_NAME;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private ImageS3UploadService imageS3UploadService;

	private String originalFilename = "test.jpg";
	private String contentType = "image/jpg";
	private String subPath = "test";
	private MockMultipartFile mockMultipartFile = new MockMultipartFile(
			"test",
			originalFilename,
			contentType,
			"test".getBytes()
	);

	@AfterAll
	static void tearDown(@Autowired S3Mock s3Mock) {
		s3Mock.stop();
	}

	@Test
	@DisplayName("Success - S3 에 이미지 업로드를 성공한다 - upload")
	void uploadSuccess() {
		// given & when
		String url = imageS3UploadService.upload(mockMultipartFile, subPath);

		// then
		assertThat(url).contains(BASE_URL + "/" + BUCKET_NAME + "/" + subPath);
	}
}