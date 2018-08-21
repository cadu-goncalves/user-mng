package com.creativedrive.user.domain.validation;

import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation for user role validation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Role {
    String message() default "User role is not valid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
