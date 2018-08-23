package com.creativedrive.user.domain;

import io.swagger.annotations.ApiModel;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * User filter model
 */
@ApiModel
public final class UserFilter {

    @PositiveOrZero(message = "{filter.page.invalid}")
    private Integer page;

    @Positive(message = "{filter.size.invalid}")
    @Max(value = 30, message = "{filter.size.invalid}")
    private Integer size;

    @NotNull(message = "{filter.fields.null}")
    private User fields;

    private List<Sort.Order> orders;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public User getFields() {
        return fields;
    }

    public void setFields(User fields) {
        this.fields = fields;
    }

    public List<Sort.Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Sort.Order> orders) {
        this.orders = orders;
    }
}

