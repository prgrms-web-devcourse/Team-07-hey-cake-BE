package com.programmers.heycake.domain.order.model.validator;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.programmers.heycake.domain.order.model.annotation.CakeSizeCheck;
import com.programmers.heycake.domain.order.model.vo.CakeSize;

public class CakeSizeValidator implements ConstraintValidator<CakeSizeCheck, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return Arrays.stream(CakeSize.values())
				.map(Enum::name)
				.anyMatch(category -> category.equals(value));
	}
}
