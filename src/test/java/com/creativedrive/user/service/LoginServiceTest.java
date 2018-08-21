package com.creativedrive.user.service;

import com.creativedrive.user.domain.User;
import com.creativedrive.user.persistence.UserRepository;
import org.jasypt.util.password.PasswordEncryptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link com.creativedrive.user.service.LoginService}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginServiceTest {

    @MockBean
    private UserRepository mockRepo;

    @MockBean
    private PasswordEncryptor mockEncryptor;

    @Autowired
    private LoginService loginService;

    private User user;

    @Before
    public void beforeEach() {
        // User fixture
        user = new User();
        user.setId("111");
        user.setProfile("ADMIN");
        user.setName("user");
        user.setPassword("password");
        user.setEmail("user@email.com");
        user.setPhone("88889999");

        // Reset mocks
        reset(mockRepo);
        reset(mockEncryptor);
    }


    /**
     * Test scenario for valid authentication
     *
     * @throws Exception
     */
    @Test
    public void itChecksUserValidAuth() throws Exception {
        // Mock behaviours
        when(mockEncryptor.encryptPassword(user.getPassword())).thenReturn("encrypted_password");
        when(mockRepo.checkAuth(user.getName(), "encrypted_password")).thenReturn(Optional.of(user));

        // Test
        Optional<User> data = loginService.checkAuth(user.getName(), user.getPassword());
        assertThat(data.isPresent(), is(true));
        assertThat(data.get().getProfile(), equalTo(user.getProfile()));

        // Check mock iteration
        verify(mockEncryptor).encryptPassword(user.getPassword());
        verify(mockRepo).checkAuth(user.getName(), "encrypted_password");
    }


    /**
     * Test scenario for invalid authentication
     *
     * @throws Exception
     */
    @Test
    public void itChecksUserInvalidAuth() throws Exception {
        // Mock behaviours
        when(mockEncryptor.encryptPassword(user.getPassword())).thenReturn("encrypted_password");
        when(mockRepo.checkAuth(user.getName(), "encrypted_password")).thenReturn(Optional.empty());

        // Test
        Optional<User> data = loginService.checkAuth(user.getName(), user.getPassword());
        assertThat(data.isPresent(), is(false));

        // Check mock iteration
        verify(mockEncryptor).encryptPassword(user.getPassword());
        verify(mockRepo).checkAuth(user.getName(), "encrypted_password");
    }
}
