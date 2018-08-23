package com.creativedrive.user.domain;

import com.creativedrive.user.domain.validation.UserField;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

    @UserField(message = "{filter.fields.asc.invalid}")
    private Set<String> asc;

    @UserField(message = "{filter.fields.desc.invalid}")
    private Set<String> desc;

    private transient Boolean locked = Boolean.FALSE;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        if (locked) {
            return;
        }
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        if (locked) {
            return;
        }
        this.size = size;
    }

    public User getFields() {
        return fields;
    }

    public void setFields(User fields) {
        if (locked) {
            return;
        }
        this.fields = fields;
    }

    public Set<String> getAsc() {
        if(locked && asc == null) {
            return Collections.emptySet();
        }
        return asc;
    }

    public void setAsc(Set<String> asc) {
        if (locked) {
            return;
        }
        this.asc = asc;
    }

    public Set<String> getDesc() {
        if(locked && desc == null) {
            return Collections.emptySet();
        }
        return desc;
    }

    public void setDesc(Set<String> desc) {
        if (locked) {
            return;
        }
        this.desc = desc;
    }

    /**
     * Sanitize filter before using
     */
    public void sanitize() {
        if (locked) {
            return;
        }

        if(asc == null || desc == null) {
            locked = Boolean.TRUE;
            return;
        }

        // Remove asc / desc conflicts
        Set<String> temp = new HashSet();
        temp.addAll(asc);
        asc.removeAll(desc);
        desc.removeAll(temp);

        // Lock
        locked = Boolean.TRUE;
    }
}

