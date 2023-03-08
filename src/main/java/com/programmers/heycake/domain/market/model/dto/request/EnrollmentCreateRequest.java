package com.programmers.heycake.domain.market.model.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

@Builder
public record EnrollmentCreateRequest(
		@NotBlank @Length(max = 10) String businessNumber,
		@NotBlank @Length(max = 10) String ownerName,
		@NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate openDate,
		@NotBlank @Length(max = 20) String marketName,
		@NotBlank @Length(max = 20) String phoneNumber,
		@NotBlank @Length(max = 10) String city,
		@NotBlank @Length(max = 10) String district,
		@NotBlank @Length(max = 80) String detailAddress,
		@NotNull @DateTimeFormat(pattern = "HH:mm") LocalTime openTime,
		@NotNull @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
		@NotBlank @Length(max = 500) String description,
		@NotNull MultipartFile businessLicenseImage,
		@NotNull MultipartFile marketImage
) {
}
