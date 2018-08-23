package com.creativedrive.user.domain;

import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 *  User page model.
 */
@ApiModel
public final class UserPage {

    private final Integer number;

    private final Integer totalPages;

    private List<User> content;

    public UserPage(Integer totalPages, Integer number, List<User> content) {
        this.content = content;
        this.number = number;
        this.totalPages = totalPages;
    }

    public Integer getNumber() {
        return number;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public List<User> getContent() {
        return content;
    }

    public void setContent(List<User> content) {
        this.content = content;
    }
}
