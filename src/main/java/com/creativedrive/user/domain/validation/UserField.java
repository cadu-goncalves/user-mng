package com.creativedrive.user.domain.validation;

import com.creativedrive.user.component.UserFieldValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation for user field validation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = UserFieldValidator.class)
public @interface UserField {
    String message() default "User field is not valid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
