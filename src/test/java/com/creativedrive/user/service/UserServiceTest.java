package com.creativedrive.user.service;

import com.creativedrive.user.domain.User;
import com.creativedrive.user.persistence.UserRepository;
import org.jasypt.util.password.PasswordEncryptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.task.TaskExecutor;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link com.creativedrive.user.persistence.UserRepository}
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository mockRepo;

    @Mock
    private TaskExecutor taskExecutor;

    @Mock
    private PasswordEncryptor mockEncryptor;

    @InjectMocks
    private UserService userService;

    private User user;

    @Before
    public void beforeEach() {
        // User fixture
        user = new User();
        user.setName("user");
        user.setPassword("password");
        user.setEmail("user@email.com");
        user.setPhone("88889999");
    }

    @Test
    public void itCreateOrUpdateUsers() throws Exception {
        // Behaviours
        mockEncryptor.encryptPassword(user.getPassword());
        mockRepo.save(user);

        // Test
        userService.createOrUpdate(user);
        verify(mockEncryptor).encryptPassword(user.getPassword());
        verify(mockRepo).save(user);
    }

}
