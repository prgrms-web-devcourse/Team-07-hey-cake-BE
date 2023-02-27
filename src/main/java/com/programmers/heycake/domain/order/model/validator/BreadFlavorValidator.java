package com.programmers.heycake.domain.order.model.validator;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.programmers.heycake.domain.order.model.annotation.BreadFlavorCheck;
import com.programmers.heycake.domain.order.model.vo.BreadFlavor;

public class BreadFlavorValidator implements ConstraintValidator<BreadFlavorCheck, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return Arrays.stream(BreadFlavor.values())
				.map(Enum::name)
				.anyMatch(category -> category.equals(value));
	}
}
