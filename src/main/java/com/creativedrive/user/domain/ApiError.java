package com.creativedrive.user.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * API error model
 */
public final class ApiError {

    private Date timestamp;

    @JsonIgnore
    private HttpStatus status;

    private Set<String> messages;

    public ApiError() {
        timestamp = new Date();
        messages = new HashSet<>();
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
        if(status == null) {
            return HttpStatus.NOT_FOUND.value();
        }
        return status.value();
    }

    public Set<String> getMessages() {
        return messages;
    }

    public void addMessages(Collection messages) {
        this.messages.addAll(messages);
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }
}
