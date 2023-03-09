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
		@Length(max = 10, message = "사업자 등록 번호는 10자까지만 입력할 수 있습니다.")
		@NotBlank(message = "사업자 등록 번호는 공백일 수 없습니다.")
		String businessNumber,
		@Length(max = 10, message = "사장님 이름은 10자까지만 입력할 수 있습니다.")
		@NotBlank(message = "사장님 이름은 공백일 수 없습니다.")
		String ownerName,
		@NotNull(message = "업체 개업일은 필수입니다.")
		@DateTimeFormat(pattern = "yyyy-MM-dd")
		LocalDate openDate,
		@Length(max = 20, message = "업체 이름은 20자까지만 입력할 수 있습니다.")
		@NotBlank(message = "업체 이름은 공백일 수 없습니다.")
		String marketName,
		@Length(max = 20, message = "업체 전화번호는 20자까지만 입력할 수 있습니다.")
		@NotBlank(message = "업체 전화번호는 공백일 수 없습니다.ㅏ")
		String phoneNumber,
		@Length(max = 10, message = "주소 - 시는 10자까지만 입력할 수 있습니다.")
		@NotBlank(message = "주소 - 시는 공백일 수 없습니다.")
		String city,
		@Length(max = 10, message = "주소 - 구는 10자까지만 입력할 수 있습니다.")
		@NotBlank(message = "주소 - 구는 공백일 수 없습니다.")
		String district,
		@Length(max = 80, message = "상세 주소는 80자까지만 입력할 수 있습니다.")
		@NotBlank(message = "상세 주소는 공백일 수 없습니다.")
		String detailAddress,
		@NotNull(message = "업체 오픈 시간은 필수입니다.")
		@DateTimeFormat(pattern = "HH:mm")
		LocalTime openTime,
		@NotNull(message = "업체 마감 시간은 필수입니다.")
		@DateTimeFormat(pattern = "HH:mm")
		LocalTime endTime,
		@Length(max = 500, message = "업체 설명은 500자까지만 입력할 수 있습니다.")
		@NotBlank(message = "업체 설명은 공백일 수 없습니다.")
		String description,
		@NotNull(message = "사업자 등록증 이미지는 필수입니다.")
		MultipartFile businessLicenseImage,
		@NotNull(message = "업체 이미지는 필수입니다.")
		MultipartFile marketImage
) {
}
