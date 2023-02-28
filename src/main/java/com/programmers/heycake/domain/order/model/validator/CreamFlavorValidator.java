package com.programmers.heycake.domain.order.model.validator;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.programmers.heycake.domain.order.model.annotation.CreamFlavorCheck;
import com.programmers.heycake.domain.order.model.vo.CreamFlavor;

public class CreamFlavorValidator implements ConstraintValidator<CreamFlavorCheck, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return Arrays.stream(CreamFlavor.values())
				.map(Enum::name)
				.anyMatch(category -> category.equals(value));
	}
}
