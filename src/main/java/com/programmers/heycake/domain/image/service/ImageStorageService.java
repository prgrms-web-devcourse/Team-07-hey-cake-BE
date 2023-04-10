package com.programmers.heycake.domain.image.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

	String upload(MultipartFile multipartFile, String subPath);

	void delete(String subPath, String savedFilename);
}
