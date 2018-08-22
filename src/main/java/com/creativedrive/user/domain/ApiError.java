package com.creativedrive.user.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

import java.util.Date;

/**
 * API error model
 */
public final class ApiError {

    private Date timestamp;

    @JsonIgnore
    private HttpStatus status;

    private String message;

    public ApiError() {
        timestamp = new Date();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    @JsonGetter("status")
    public Integer getStatusCode() {
        return status.value();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
