package com.creativedrive.user.utils;

import com.creativedrive.user.domain.UserFilter;
import org.springframework.data.domain.Sort;

/**
 * Filter facilities
 */
public class FilterUtils {

    // Utility class private constructor
    private FilterUtils() {

    }

    /**
     * Build sort object based on filter
     *
     * @param filter {@link UserFilter}
     * @return {@link Sort}
     */
    public static Sort buildSort(final UserFilter filter) {
        // Extract filter sorts
        filter.sanitize();

        // Java 9 makes this much prettier :)
        String[] asc = filter.getAsc().toArray(new String[filter.getAsc().size()]);
        String[] desc = filter.getDesc().toArray(new String[filter.getDesc().size()]);

        // No valid start state for Sort, so...
        Sort sort = null;
        if(asc.length > 0) {
            sort = Sort.by(Sort.Direction.ASC, asc);
        }
        if(desc.length > 0) {
            if(sort == null) {
                sort = Sort.by(Sort.Direction.DESC, desc);
            } else {
                sort.and(Sort.by(Sort.Direction.DESC, desc));
            }
        }

        // No sorts
        if(sort == null) {
            sort = Sort.unsorted();
        }

        // Result
        return sort;
    }
}
