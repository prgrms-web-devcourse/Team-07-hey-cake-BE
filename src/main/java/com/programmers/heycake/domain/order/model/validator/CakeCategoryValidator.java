package com.programmers.heycake.domain.order.model.validator;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.programmers.heycake.domain.order.model.annotation.CakeCategoryCheck;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;

public class CakeCategoryValidator implements ConstraintValidator<CakeCategoryCheck, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return Arrays.stream(CakeCategory.values())
				.map(Enum::name)
				.anyMatch(category -> category.equals(value));
	}
}
