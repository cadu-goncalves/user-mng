package com.creativedrive.user.component;

import com.creativedrive.user.domain.UserProfile;
import com.creativedrive.user.domain.validation.Profile;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Custom bean validator
 *
 * @see {@link Profile}
 */
@Component
public class UserProfileValidator implements ConstraintValidator<Profile, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) {
            return false;
        }

        return UserProfile.values().contains(value.toUpperCase());
    }
}