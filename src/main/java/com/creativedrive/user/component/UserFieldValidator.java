package com.creativedrive.user.component;

import com.creativedrive.user.domain.User;
import com.creativedrive.user.domain.validation.UserField;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

/**
 * Custom bean validator
 *
 * @see {@link UserField}
 */
@Component
public class UserFieldValidator implements ConstraintValidator<UserField, Set<String>> {

    @Override
    public boolean isValid(Set<String> value, ConstraintValidatorContext context) {
        if(value == null || value.isEmpty()) {
            return true;
        }

        try {
            for (String v : value) {
                User.class.getDeclaredField(v);
            }
            // OK
            return true;
        } catch (NoSuchFieldException e) {
            // Failed
            return false;
        }

    }
}
