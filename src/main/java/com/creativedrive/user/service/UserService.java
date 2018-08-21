package com.creativedrive.user.service;

import com.creativedrive.user.domain.User;
import com.creativedrive.user.domain.UserException;
import com.creativedrive.user.domain.UserRole;
import com.creativedrive.user.persistence.UserRepository;
import com.creativedrive.user.utils.MessageUtils;
import org.jasypt.util.password.PasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Optional;
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

    /**
     * Create new user
     *
     * @param user {@link User} entity to create
     * @return {@link CompletableFuture<User>}
     */
    @Secured(UserRole.ADMIN)
    public CompletableFuture<User> create(final User user) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Create user: " + user.getName());

            // Encrypt password and save
            user.setPassword(encryptor.encryptPassword(user.getPassword()));
            userRepo.save(user);
            return user;
        }, executor);
    }

    /**
     * Update existing user
     *
     * @param user {@link User} entity to update
     * @return {@link CompletableFuture<User>}
     * @throws UserException if informed user id not macth database value
     */
    @Secured(UserRole.ADMIN)
    public CompletableFuture<User> update(final User user) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Update user: " + user.getName());

            // Check sensitive data
            Optional<User> findResult = userRepo.findByName(user.getName());
            if(!findResult.isPresent()) {
                // Not found
                String message = MessageUtils.getMessage("messages", "user.update.notfound");
                throw new UserException(message);
            }

            User currentUser = findResult.get();
            if(!currentUser.equals(user)) {
                // Wrong id
                String message = MessageUtils.getMessage("messages", "user.update.denied");
                throw new UserException(message);
            }

            if(!user.getPassword().equals(currentUser.getPassword())) {
                // Password changed, encrypt
                user.setPassword(encryptor.encryptPassword(user.getPassword()));
            }

            userRepo.save(user);
            return user;
        }, executor);
    }

    /**
     * Delete existing user.
     *
     * For security reasons an user cannot remove itself
     *
     * @param user {@link User} entity to remove
     * @return {@link CompletableFuture<Void>}
     */
    @Secured(UserRole.ADMIN)
    @PreAuthorize("#user != authentication.name")
    public CompletableFuture<Void> delete(final String user) {
        return CompletableFuture.runAsync(() -> {
            LOGGER.info("Delete user: " + user);

            Optional<User> findResult = userRepo.findByName(user);
            if(findResult.isPresent()) {
                userRepo.delete(findResult.get());
            }
        }, executor);
    }
}
