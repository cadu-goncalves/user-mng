package com.creativedrive.user.utils;

import com.creativedrive.user.domain.ApiError;
import com.creativedrive.user.domain.CrudError;
import com.creativedrive.user.domain.UserException;
import org.springframework.http.HttpStatus;

/**
 * Api error facilities
 */
public class ApiErrorBuilder {

    // Utility class private constructor
    private ApiErrorBuilder() {

    }

    /**
     * Build API error
     *
     * @param throwable {@link Throwable} exception
     * @return {@link ApiError}
     */
    public static ApiError build(Throwable throwable) {
        ApiError error = new ApiError();

        // Pick cause
        if(throwable.getCause() instanceof UserException) {
            UserException exception = (UserException) throwable.getCause();
            error.setMessage(exception.getMessage());
            error.setStatus(translateError(exception.getError()));
        } else {
            error.setMessage(MessageUtils.getMessage("messages", "error"));
            error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return error;
    }


    /**
     * Translate error to HTTP status (common cases)
     *
     * @param code {@link CrudError}
     * @return {@link org.springframework.http.HttpStatus}
     */
    public static HttpStatus translateError(CrudError code) {
        switch (code) {
            case CREATE_ERROR:
            case UPDATE_ERROR:
                return  HttpStatus.BAD_REQUEST;

            case RETRIEVE_ERROR:
            case DELETE_ERROR:
                return  HttpStatus.NOT_FOUND;

            default:
                return  HttpStatus.NOT_FOUND;
        }
    }
}
