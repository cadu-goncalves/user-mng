package com.creativedrive.user.service;

import com.creativedrive.user.domain.User;
import com.creativedrive.user.persistence.UserRepository;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Login service implementation.
 */
@Service
public class LoginService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncryptor encryptor;

    /**
     * Verify if access credentials are valid.
     *
     * @param name
     * @param password
     * @return The user role for matching credentials or </null> if not match
     */
    public Optional<User> checkAuth(String name, String password) {
        // Encrypt the password
        final String encryptedPwd = encryptor.encryptPassword(password);
        // Find
        return userRepo.checkAuth(name, encryptedPwd);
    }
}
