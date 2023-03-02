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
		@NotNull @Positive
		Integer hopePrice,
		@NotBlank
		String region,
		@NotBlank @Length(max = 20)
		String title,
		@NotNull
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime visitTime,
		@NotNull
		CakeCategory cakeCategory,
		@NotNull
		CakeSize cakeSize,
		@NotNull
		CakeHeight cakeHeight,
		@NotNull
		BreadFlavor breadFlavor,
		@NotNull
		CreamFlavor creamFlavor,
		@NotBlank
		String requirements,
		@NotNull
		List<MultipartFile> cakeImages
) {
}