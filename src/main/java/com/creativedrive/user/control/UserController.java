package com.creativedrive.user.control;

import com.creativedrive.user.domain.ApiError;
import com.creativedrive.user.domain.User;
import com.creativedrive.user.service.UserService;
import com.creativedrive.user.utils.ApiErrorBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

/**
 * User API controller
 */
@Api(authorizations = {@Authorization(value = "BasicAuth")})
@RestController
@RequestMapping(
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE
})
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * User create endpoint
     *
     * @param user {@link User} from request body
     * @return {@link DeferredResult} for chunked HTTP response
     */
    @ApiOperation(value = "Create new user", response = User.class)
    @PostMapping("api/user")
    public @ResponseBody
    DeferredResult<ResponseEntity> create(final @RequestBody @Valid User user) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<User> future = userService.create(user);
        future.whenCompleteAsync(
                (result, throwable) -> {
                    if (throwable != null) {
                        ApiError error = ApiErrorBuilder.build(throwable);
                        response.setErrorResult(new ResponseEntity<>(error, error.getStatus()));
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
    @ApiOperation(value = "Retrieve user", response = User.class)
    @GetMapping(value = "api/user/{userName}")
    public @ResponseBody
    DeferredResult<ResponseEntity> retrieve(final @PathVariable String userName) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<User> future = userService.retrieve(userName);
        future.whenCompleteAsync(
                (result, throwable) -> {
                    if (throwable != null) {
                        ApiError error = ApiErrorBuilder.build(throwable);
                        response.setErrorResult(new ResponseEntity<>(error, error.getStatus()));
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
     * @param user     {@link User} from request body
     * @param userName {@link String} from URI path
     * @return {@link DeferredResult} for chunked HTTP response
     */
    @ApiOperation(value = "Update user", response = User.class)
    @PreAuthorize("#user.name == #userName")
    @PutMapping(value = "api/user/{userName}")
    public @ResponseBody
    DeferredResult<ResponseEntity> update(final @RequestBody @Valid User user,
                                          final @PathVariable String userName) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<User> future = userService.update(user);
        future.whenCompleteAsync(
                (result, throwable) -> {
                    if (throwable != null) {
                        ApiError error = ApiErrorBuilder.build(throwable);
                        response.setErrorResult(new ResponseEntity<>(error, error.getStatus()));
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
    @ApiOperation(value = "Delete user")
    @DeleteMapping(value = "api/user/{userName}")
    public @ResponseBody
    DeferredResult<ResponseEntity> delete(final @PathVariable String userName) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<Void> future = userService.delete(userName);
        future.whenCompleteAsync(
                (result, throwable) -> {
                    if (throwable != null) {
                        ApiError error = ApiErrorBuilder.build(throwable);
                        response.setErrorResult(new ResponseEntity<>(error, error.getStatus()));
                    } else {
                        response.setResult(new ResponseEntity<>(HttpStatus.OK));
                    }
                }
        );
        return response;
    }
}
