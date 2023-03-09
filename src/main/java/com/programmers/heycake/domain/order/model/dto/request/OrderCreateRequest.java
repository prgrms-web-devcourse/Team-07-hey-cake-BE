package com.programmers.heycake.domain.order.model.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.programmers.heycake.domain.order.model.vo.BreadFlavor;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
import com.programmers.heycake.domain.order.model.vo.CakeHeight;
import com.programmers.heycake.domain.order.model.vo.CakeSize;
import com.programmers.heycake.domain.order.model.vo.CreamFlavor;

import lombok.Builder;

@Builder
public record OrderCreateRequest(
		@NotNull(message = "희망 가격은 필수입니다.")
		@Positive(message = "희망 가격은 양수여야합니다.")
		Integer hopePrice,
		@NotBlank(message = "희망 지역은 공백일 수 없습니다.")
		String region,
		@Length(max = 20, message = "주문 제목은 20자까지 입력할 수 있습니다.")
		@NotBlank(message = "주문 제목은 공백일 수 없습니다.")
		String title,
		@NotNull(message = "희망 방문 시간은 필수입니다.")
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime visitTime,
		@NotNull(message = "케익 카테고리는 필수입니다.")
		CakeCategory cakeCategory,
		@NotNull(message = "케익 크기는 필수입니다.")
		CakeSize cakeSize,
		@NotNull(message = "케익 높이는 필수입니다.")
		CakeHeight cakeHeight,
		@NotNull(message = "빵 맛은 필수입니다.")
		BreadFlavor breadFlavor,
		@NotNull(message = "크림 맛은 필수입니다.")
		CreamFlavor creamFlavor,
		@NotBlank(message = "추가 요구사항은 공백일 수 없습니다.")
		String requirements,
		@NotNull(message = "케익 이미지는 필수입니다.")
		List<MultipartFile> cakeImages
) {
}