package com.creativedrive.user.domain;

/**
 * User exception
 */
public class UserException extends RuntimeException {

    private CrudError error;

    public UserException(String message, CrudError error) {
        super(message);
        this.error = error;
    }

    public CrudError getError() {
        return error;
    }

}
