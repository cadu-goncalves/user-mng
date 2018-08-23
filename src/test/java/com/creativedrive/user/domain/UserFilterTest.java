package com.creativedrive.user.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

/**
 * Test for {@link UserFilter}
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UserFilterTest {

    private UserFilter filter;

    private Set<String> setA;

    private Set<String> setB;


    @Before
    public void beforeEach() {
        filter = new UserFilter();
        setA = new HashSet<>();
        setB = new HashSet<>();
    }

    /**
     * Test scenario for nulls
     */
    @Test
    public void itSanitizesNull() {
        // Fixtures
        filter.setAsc(null);
        filter.setDesc(null);

        // Test
        filter.sanitize();
        assertThat(filter.getAsc(), notNullValue());
        assertThat(filter.getDesc(), notNullValue());
    }

    /**
     * Test scenario for partial sanitize
     */
    @Test
    public void itSanitizesPartial() {
        // Fixtures
        setA.addAll(Arrays.asList(new String[]{"v1", "v2"}));
        filter.setAsc(setA);
        setB.addAll(Arrays.asList(new String[]{"v1", "v2", "v3"}));
        filter.setDesc(setB);

        // Test
        filter.sanitize();
        assertThat(filter.getAsc(), not(hasItems("v1", "v2")));
        assertThat(filter.getDesc(), not(hasItems("v1", "v2")));
        assertThat(filter.getDesc(), hasItems("v3"));
    }

    /**
     * Test scenario for full sanitize
     */
    @Test
    public void itSanitizesAll() {
        // Fixtures
        setA.addAll(Arrays.asList(new String[]{"v1", "v2", "v3"}));
        filter.setAsc(setA);
        setB.addAll(Arrays.asList(new String[]{"v1", "v2", "v3"}));
        filter.setDesc(setB);

        // Test
        filter.sanitize();
        assertThat(filter.getAsc(), empty());
        assertThat(filter.getDesc(), empty());
    }

    /**
     * Test scenario for no sanitize
     */
    @Test
    public void itSanitizesNone() {
        // Fixtures
        setA.addAll(Arrays.asList(new String[]{"v1"}));
        filter.setAsc(setA);
        setB.addAll(Arrays.asList(new String[]{"v3"}));
        filter.setDesc(setB);

        // Test
        filter.sanitize();
        assertThat(filter.getAsc(), hasItems("v1"));
        assertThat(filter.getAsc(), not(hasItems("v3")));
        assertThat(filter.getDesc(), hasItems("v3"));
        assertThat(filter.getDesc(), not(hasItems("v1")));
    }

    /**
     * Test scenario for sanitize locking
     */
    @Test
    public void itLocks() {
        // Fixtures
        setA.addAll(Arrays.asList(new String[]{"v1"}));
        filter.setAsc(setA);
        setB.addAll(Arrays.asList(new String[]{"v3"}));
        filter.setDesc(setB);

        // Test
        filter.sanitize();
        filter.setAsc(filter.getDesc());
        assertThat(filter.getAsc(), not(equalTo(filter.getDesc())));
    }

}
