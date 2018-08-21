package com.creativedrive.user.service;

import com.creativedrive.user.domain.User;
import com.creativedrive.user.persistence.UserRepository;
import org.jasypt.util.password.PasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * User service implementation.
 */
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncryptor encryptor;

    @Autowired
    private TaskExecutor executor;

    public CompletableFuture<User> createOrUpdate(final User user) {
        return CompletableFuture.supplyAsync(() -> {
            // Encrypt password and save
            user.setPassword(encryptor.encryptPassword(user.getPassword()));
            userRepo.save(user);
            return user;
        }, executor);
    }

}
