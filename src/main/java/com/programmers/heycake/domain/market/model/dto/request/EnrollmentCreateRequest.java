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
		@Length(max = 10) @NotBlank String businessNumber,
		@Length(max = 10) @NotBlank String ownerName,
		@NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate openDate,
		@Length(max = 20) @NotBlank String marketName,
		@Length(max = 20) @NotBlank String phoneNumber,
		@Length(max = 10) @NotBlank String city,
		@Length(max = 10) @NotBlank String district,
		@Length(max = 80) @NotBlank String detailAddress,
		@NotNull @DateTimeFormat(pattern = "HH:mm") LocalTime openTime,
		@NotNull @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
		@Length(max = 500) @NotBlank String description,
		@NotNull MultipartFile businessLicenseImage,
		@NotNull MultipartFile marketImage
) {
}
