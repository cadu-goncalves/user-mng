package com.creativedrive.user.domain.validation;

import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation for user profile validation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IsUserProfile {
    String message() default "User profile is not valid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
