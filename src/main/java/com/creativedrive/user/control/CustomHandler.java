package com.creativedrive.user.control;

import com.creativedrive.user.domain.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles overall exceptions
 */
@ControllerAdvice
@RestController
public class CustomHandler {

    /**
     * Custom error output when {@link MethodArgumentNotValidException} occurs.
     *
     * @param exception
     * @return {@link ResponseEntity} with errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException exception) {
        ApiError error = new ApiError();

        // Collect validation messages
       exception.getBindingResult()
                .getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .forEach(error::addMessage);

        error.setStatus(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(error, error.getStatus());
    }

}
