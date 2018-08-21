package com.creativedrive.user.component;

import com.creativedrive.user.domain.UserRole;
import com.creativedrive.user.domain.validation.Role;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Custom bean validator
 *
 * @see {@link Role}
 */
@Component
public class UserRoleValidator implements ConstraintValidator<Role, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) {
            return false;
        }

        return UserRole.values().contains(value.toUpperCase());
    }
}