package com.creativedrive.user.service;

import com.creativedrive.user.domain.*;
import com.creativedrive.user.persistence.UserRepository;
import com.creativedrive.user.utils.FilterUtils;
import com.creativedrive.user.utils.MessageUtils;
import org.jasypt.util.password.PasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.springframework.data.domain.ExampleMatcher.StringMatcher;
import static org.springframework.data.domain.ExampleMatcher.matching;

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
    @Secured(UserProfile.ADMIN)
    public CompletableFuture<User> create(final User user) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Create user: " + user.getName());

            Optional<User> findResult = userRepo.findByName(user.getName());
            if (findResult.isPresent()) {
                // Already exists
                String message = MessageUtils.getMessage("messages", "user.create.denied");
                throw new UserException(message, CrudError.CREATE_ERROR);
            }

            // Encrypt password and save
            user.setPassword(encryptor.encryptPassword(user.getPassword()));
            user.setId(null);
            userRepo.save(user);
            return user;
        }, executor).exceptionally(throwable -> {
            LOGGER.error(throwable.getMessage());
            throw translateException(throwable);
        });
    }

    /**
     * Retrieve existing user
     *
     * @param userName {@link String} User name to retrieve
     * @return {@link CompletableFuture<User>}
     * @throws UserException if no matches found for {@link User#getName()}
     */
    @Secured({UserProfile.ADMIN, UserProfile.USER})
    public CompletableFuture<User> retrieve(final String userName) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Retrieve user: " + userName);

            Optional<User> findResult = userRepo.findByName(userName);
            if (!findResult.isPresent()) {
                // Not found
                String message = MessageUtils.getMessage("messages", "user.notfound");
                throw new UserException(message, CrudError.RETRIEVE_ERROR);
            }

            return findResult.get();
        }, executor).exceptionally(throwable -> {
            LOGGER.error(throwable.getMessage());
            throw translateException(throwable);
        });
    }

    /**
     * Update existing user
     *
     * @param user {@link User} entity to update
     * @return {@link CompletableFuture<User>}
     * @throws UserException if {@link User#equals(Object)} not matches database value
     */
    @Secured(UserProfile.ADMIN)
    public CompletableFuture<User> update(final User user) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Update user: " + user.getName());

            // Check sensitive data
            Optional<User> findResult = userRepo.findByName(user.getName());
            if (!findResult.isPresent()) {
                // Not found
                String message = MessageUtils.getMessage("messages", "user.notfound");
                throw new UserException(message, CrudError.UPDATE_ERROR);
            }

            User currentUser = findResult.get();
            if (!currentUser.equals(user)) {
                // Wrong id
                String message = MessageUtils.getMessage("messages", "user.update.denied");
                throw new UserException(message, CrudError.UPDATE_ERROR);
            }

            if (!user.getPassword().equals(currentUser.getPassword())) {
                // Password changed, encrypt
                user.setPassword(encryptor.encryptPassword(user.getPassword()));
            }

            // Save
            userRepo.save(user);
            return user;
        }, executor).exceptionally(throwable -> {
            LOGGER.error(throwable.getMessage());
            throw translateException(throwable);
        });
    }

    /**
     * Delete existing user.
     * <p>
     * For security reasons an user cannot remove itself
     *
     * @param userName {@link String} {@link User#getName()} to remove
     * @return {@link CompletableFuture<Void>}
     */
    @Secured(UserProfile.ADMIN)
    @PreAuthorize("#userName != authentication.name")
    public CompletableFuture<Void> delete(final String userName) {
        return CompletableFuture.runAsync(() -> {
            LOGGER.info("Delete user: " + userName);

            Optional<User> findResult = userRepo.findByName(userName);
            if (findResult.isPresent()) {
                userRepo.delete(findResult.get());
            }
        }, executor).exceptionally(throwable -> {
            LOGGER.error(throwable.getMessage());
            throw translateException(throwable);
        });
    }

    /**
     * Find existing users based defined filter.
     *
     * @param filter {@link UserFilter} filter values
     * @return {@link CompletableFuture<Page<User>>}
     */
    @Secured({UserProfile.ADMIN, UserProfile.USER})
    public CompletableFuture<UserPage> findUsers(final UserFilter filter) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Search users: ");

            // Filter by example (basic approach)
            Example<User> example = Example.of(filter.getFields(),
                    matching().withStringMatcher(StringMatcher.REGEX)
            );

            // Sort
            Sort sort = FilterUtils.buildSort(filter);

            // Paginate
            PageRequest reqPage = PageRequest.of(filter.getPage(), filter.getSize(), sort);

            // Find
            Page<User> page = userRepo.findAll(example, reqPage);
            return new UserPage(page.getTotalPages(), page.getNumber(), page.getContent());
        }, executor).exceptionally(throwable -> {
            LOGGER.error(throwable.getMessage());
            throw translateException(throwable);
        });
    }


    /**
     * Translate relevant exceptions into {@link UserException}
     *
     * @param throwable {@link Throwable} original exception
     * @return {@link UserException}
     */
    private UserException translateException(Throwable throwable) {
        String message;
        UserException exception;

        // Unwrap
        if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
        }

        // Check
        if (throwable instanceof UserException) {
            // Nothing to do
            return (UserException) throwable;
        } else if (throwable instanceof DataIntegrityViolationException) {
            message = MessageUtils.getMessage("messages", "user.constraint.error");
            exception = new UserException(message, throwable, CrudError.CONSTRAINT_ERROR);
        } else if (throwable instanceof DataAccessException) {
            message = MessageUtils.getMessage("messages", "user.access.error");
            exception = new UserException(message, throwable, CrudError.IO_ERROR);
        } else {
            message = MessageUtils.getMessage("messages", "error");
            exception = new UserException(message, throwable, CrudError.ERROR);
        }

        return exception;
    }

}
