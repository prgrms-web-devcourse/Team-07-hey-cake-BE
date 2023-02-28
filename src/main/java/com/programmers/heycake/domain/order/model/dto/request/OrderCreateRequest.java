package com.programmers.heycake.domain.order.model.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.programmers.heycake.domain.order.model.annotation.BreadFlavorCheck;
import com.programmers.heycake.domain.order.model.annotation.CakeCategoryCheck;
import com.programmers.heycake.domain.order.model.annotation.CakeHeightCheck;
import com.programmers.heycake.domain.order.model.annotation.CakeSizeCheck;
import com.programmers.heycake.domain.order.model.annotation.CreamFlavorCheck;

import lombok.Builder;

@Builder
public record OrderCreateRequest(
		@NotNull @Positive Integer hopePrice,
		@NotBlank String region,
		@NotBlank @Length(max = 20) String title,
		@NotNull
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime visitTime,
		@CakeCategoryCheck String cakeCategory,
		@CakeSizeCheck String cakeSize,
		@CakeHeightCheck String cakeHeight,
		@BreadFlavorCheck String breadFlavor,
		@CreamFlavorCheck String creamFlavor,
		String requirements,
		List<MultipartFile> cakeImages
) {
}
