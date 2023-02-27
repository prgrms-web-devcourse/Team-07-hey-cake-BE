package com.programmers.heycake.domain.order.model.validator;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.programmers.heycake.domain.order.model.annotation.CakeHeightCheck;
import com.programmers.heycake.domain.order.model.vo.CakeHeight;

public class CakeHeightValidator implements ConstraintValidator<CakeHeightCheck, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return Arrays.stream(CakeHeight.values())
				.map(Enum::name)
				.anyMatch(category -> category.equals(value));
	}
}
