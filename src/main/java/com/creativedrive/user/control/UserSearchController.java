package com.creativedrive.user.control;

import com.creativedrive.user.domain.ApiError;
import com.creativedrive.user.domain.UserFilter;
import com.creativedrive.user.domain.UserPage;
import com.creativedrive.user.service.UserService;
import com.creativedrive.user.utils.ApiErrorBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

/**
 * User Search API controller
 */
@Api(authorizations = {@Authorization(value = "BasicAuth")})
@RestController
@RequestMapping(
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE
        })
public class UserSearchController {

    @Autowired
    private UserService userService;

    /**
     * User search endpoint
     *
     * @param filter {@link UserFilter} from request body
     * @return {@link DeferredResult} for chunked HTTP response
     */
    @ApiOperation(value = "Search users", response = UserPage.class)
    @PostMapping(value = "api/users")
    public @ResponseBody
    DeferredResult<ResponseEntity> search(final @RequestBody @Valid UserFilter filter) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<UserPage> future = userService.findUsers(filter);
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
}
