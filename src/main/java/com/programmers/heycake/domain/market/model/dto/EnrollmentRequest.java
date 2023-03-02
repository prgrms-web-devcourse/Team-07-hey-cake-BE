package com.programmers.heycake.domain.market.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

@Builder
public record EnrollmentRequest(
		@NotBlank String businessNumber,
		@NotBlank String ownerName,
		@NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate openDate,
		@NotBlank String marketName,
		@NotBlank String phoneNumber,
		@NotBlank String city,
		@NotBlank String district,
		@NotBlank String detailAddress,
		@NotNull @DateTimeFormat(pattern = "HH:mm") LocalTime openTime,
		@NotNull @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
		@NotBlank String description,
		@NotNull MultipartFile businessLicenseImage,
		@NotNull MultipartFile marketImage
) {
}
