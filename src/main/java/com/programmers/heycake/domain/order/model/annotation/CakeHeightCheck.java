package com.programmers.heycake.domain.order.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import com.programmers.heycake.domain.order.model.validator.CakeHeightValidator;
import com.programmers.heycake.domain.order.model.validator.CakeSizeValidator;
import com.programmers.heycake.domain.order.model.vo.CakeHeight;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CakeHeightValidator.class)
public @interface CakeHeightCheck {
	String message() default "잘못된 케익 높이입니다.";
	Class[] groups() default {};
	Class[] payload() default {};
}
