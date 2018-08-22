package com.creativedrive.user.control;

import com.creativedrive.user.domain.User;
import com.creativedrive.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

/**
 * User API controller
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * User create endpoint
     *
     * @param user {@link User} from request body
     * @return {@link DeferredResult} for chunked HTTP response
     */
    @PostMapping("api/user")
    public @ResponseBody DeferredResult<ResponseEntity> create(final @RequestBody @Valid User user) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<User> future = userService.create(user);
        future.whenCompleteAsync(
                (result, throwable) -> {
                    if (throwable != null) {
                        response.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                    } else {
                        response.setResult(new ResponseEntity<>(result, HttpStatus.CREATED));
                    }
                }
        );
        return response;
    }

    /**
     * User retrieve endpoint
     *
     * @param userName {@link String} from URI path
     * @return {@link DeferredResult} for chunked HTTP response
     */
    @GetMapping(value = "api/user/{userName}")
    public @ResponseBody DeferredResult<ResponseEntity> retrieve(final @PathVariable String userName) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<User> future = userService.retrieve(userName);
        future.whenCompleteAsync(
                (result, throwable) -> {
                    if (throwable != null) {
                        response.setResult(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                    } else {
                        response.setResult(new ResponseEntity<>(result, HttpStatus.OK));
                    }
                }
        );
        return response;
    }

    /**
     * User update endpoint
     *
     * @param user {@link User} from request body
     * @param userName {@link String} from URI path
     * @return {@link DeferredResult} for chunked HTTP response
     */
    @PutMapping(value = "api/user/{userName}")
    @PreAuthorize("#user.name == #userName")
    public @ResponseBody DeferredResult<ResponseEntity> update(final @RequestBody @Valid User user,
                                                               final @PathVariable String userName) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<User> future = userService.update(user);
        future.whenCompleteAsync(
                (result, throwable) -> {
                    if (throwable != null) {
                        response.setResult(new ResponseEntity<>(HttpStatus.NOT_MODIFIED));
                    } else {
                        response.setResult(new ResponseEntity<>(result, HttpStatus.OK));
                    }
                }
        );
        return response;
    }

    /**
     * User delete endpoint
     *
     * @param userName {@link String} from URI path
     * @return {@link DeferredResult} for chunked HTTP response
     */
    @DeleteMapping(value = "api/user/{userName}")
    public @ResponseBody DeferredResult<ResponseEntity> delete(final @PathVariable String userName) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<Void> future = userService.delete(userName);
        future.whenCompleteAsync(
                (result, throwable) -> {
                    if (throwable != null) {
                        response.setResult(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                    } else {
                        response.setResult(new ResponseEntity<>(HttpStatus.OK));
                    }
                }
        );
        return response;
    }
}
