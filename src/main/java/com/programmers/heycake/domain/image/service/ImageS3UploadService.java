package com.programmers.heycake.domain.image.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageS3UploadService implements ImageUploadService {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Override
	public String upload(MultipartFile multipartFile, String subPath) {
		String originalFilename = multipartFile.getOriginalFilename();
		String savedFilename = createSavedFilename(originalFilename);

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(multipartFile.getContentType());

		try {
			amazonS3.putObject(new PutObjectRequest(bucketName, subPath, multipartFile.getInputStream(), objectMetadata));
		} catch (IOException e) {
			throw new IllegalStateException("이미지를 업로드할 수 없습니다.", e);
		}

		return amazonS3.getUrl(bucketName, subPath + "/" + savedFilename).toString();
	}

	@Override
	public void delete(String subPath, String savedFilename) {
		amazonS3.deleteObject(bucketName, subPath + "/" + savedFilename);
	}

	private String createSavedFilename(String originalFilename) {
		String uuid = UUID.randomUUID().toString();
		return uuid + "." + extractExtension(originalFilename);
	}

	private String extractExtension(String originalFilename) {
		int beforeExtensionIndex = originalFilename.lastIndexOf(".");
		return originalFilename.substring(beforeExtensionIndex + 1);
	}
}
