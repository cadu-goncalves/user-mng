package com.creativedrive.user.persistence;

import com.creativedrive.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User repository tests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    private User user;

    @Before
    public void beforeEach() {
        // Clear database
        repository.deleteAll();

        // Fixtures
        user = new User();
        user.setEmail("user.email.com");
        user.setName("user");
        user.setPassword("pass123");
    }

    /**
     * Test findByName query
     *
     * @throws Exception
     */
    @Test
    public void itFindsByName() throws Exception {
        // Save arbitrary user
        repository.save(user);

        // Test found
        Optional<User> data = repository.findByName(user.getName());
        assertThat(data.isPresent(), is(true));
        assertThat(data.get(), equalTo(user));

        // Test not found
        data = repository.findByName("who??");
        assertThat(data.isPresent(), is(false));
    }

    /**
     * Test checkAuth query
     *
     * @throws Exception
     */
    @Test
    public void itChecksAuth() throws Exception {
        // Save arbitrary user
        repository.save(user);

        // Test found
        Optional<User> data = repository.checkAuth(user.getName(), user.getPassword());
        assertThat(data.isPresent(), is(true));
        assertThat(data.get(), equalTo(user));

        // Test not found
        data = repository.checkAuth(user.getName(), "wrong");
        assertThat(data.isPresent(), is(false));
    }
}
