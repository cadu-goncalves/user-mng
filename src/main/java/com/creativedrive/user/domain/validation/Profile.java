package com.creativedrive.user.domain.validation;

import com.creativedrive.user.component.UserProfileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation for user profile validation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = UserProfileValidator.class)
public @interface Profile {
    String message() default "User profile is not valid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
