package com.programmers.heycake.domain.order.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import com.programmers.heycake.domain.order.model.validator.BreadFlavorValidator;
import com.programmers.heycake.domain.order.model.validator.CreamFlavorValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreamFlavorValidator.class)
public @interface CreamFlavorCheck {
	String message() default "잘못된 케익 사이즈입니다.";
	Class[] groups() default {};
	Class[] payload() default {};
}
