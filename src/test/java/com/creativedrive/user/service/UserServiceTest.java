package com.creativedrive.user.service;

import com.creativedrive.user.domain.User;
import com.creativedrive.user.domain.UserException;
import com.creativedrive.user.domain.UserProfile;
import com.creativedrive.user.persistence.UserRepository;
import org.jasypt.util.password.PasswordEncryptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link com.creativedrive.user.service.UserService}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @MockBean
    private UserRepository mockRepo;

    @MockBean
    private PasswordEncryptor mockEncryptor;

    @Autowired
    private UserService userService;

    private User user;

    @Before
    public void beforeEach() {
        // User fixture
        user = new User();
        user.setId("111");
        user.setName("user");
        user.setPassword("password");
        user.setEmail("user@email.com");
        user.setPhone("88889999");

        // Reset mocks
        reset(mockRepo);
        reset(mockEncryptor);
    }

    /**
     * Test scenario where new user is created
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itCreatesUsers() throws Exception {
        // Mock behaviours
        when(mockEncryptor.encryptPassword(user.getPassword())).thenReturn("encrypted_password");
        when(mockRepo.findByName(user.getName())).thenReturn(Optional.empty());
        when(mockRepo.save(user)).thenReturn(user);

        // Test
        userService.create(user).get();
        assertThat(user.getPassword(), equalTo("encrypted_password"));

        // Check mock iteration
        verify(mockEncryptor).encryptPassword("password");
        verify(mockRepo).findByName(user.getName());
        verify(mockRepo).save(user);
    }

    /**
     * Test scenario where a user that already exists is sent to create
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itDeniesCreateIfUserRepeated() throws Exception {
        // Mock behaviours
        when(mockEncryptor.encryptPassword(user.getPassword())).thenReturn("encrypted_password");
        when(mockRepo.findByName(user.getName())).thenReturn(Optional.of(user));

        // Test (must throw exception)
        try {
            userService.create(user).get();
        } catch (Exception e) {
            if (!(e.getCause() instanceof UserException)) {
                Assert.fail();
            }
        }
    }

    /**
     * Test scenario where user is retrieved
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.USER})
    public void itRetrievesUsers() throws Exception {
        // Mock behaviours
        when(mockRepo.findByName(user.getName())).thenReturn(Optional.of(user));

        // Test
        User result = userService.retrieve(user.getName()).get();
        assertThat(result, equalTo(user));

        // Check mock iteration
        verify(mockRepo, only()).findByName(user.getName());
    }

    /**
     * Test scenario where user is retrieved but not exists
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.USER})
    public void itDeniesRetrieveIfUserNotExist() throws Exception {
        // Mock behaviours
        when(mockRepo.findByName(user.getName())).thenReturn(Optional.empty());

        // Test (must throw exception)
        try {
            userService.retrieve(user.getName()).get();
        } catch (Exception e) {
            if (!(e.getCause() instanceof UserException)) {
                Assert.fail();
            }
        }
    }

    /**
     * Test scenario where user password is not changed
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itUpdatesUsersKeepingCurrentPassword() throws Exception {
        User storedUser = new User();
        BeanUtils.copyProperties(user, storedUser);

        // Mock behaviours
        when(mockEncryptor.encryptPassword(user.getPassword())).thenReturn("other_encrypted_password");
        when(mockRepo.findByName("user")).thenReturn(Optional.of(storedUser));
        when(mockRepo.save(user)).thenReturn(user);

        // Test
        userService.update(user).get();

        // Check mock iteration
        verify(mockEncryptor, never()).encryptPassword(anyString());
        verify(mockRepo).save(user);
    }

    /**
     * Test scenario where user password is changed
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itUpdatesUsersEncryptingNewPassword() throws Exception {
        User storedUser = new User();
        BeanUtils.copyProperties(user, storedUser);
        storedUser.setPassword("encrypted_password");

        // Mock behaviours
        when(mockEncryptor.encryptPassword(user.getPassword())).thenReturn("other_encrypted_password");
        when(mockRepo.findByName("user")).thenReturn(Optional.of(storedUser));
        when(mockRepo.save(user)).thenReturn(user);

        // Test
        userService.update(user).get();

        // Check mock iteration
        verify(mockEncryptor).encryptPassword("password");
        verify(mockRepo).save(user);
    }

    /**
     * Test scenario where user id not matches provided user
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itDeniesUpdateIfUserIdNotValid() {
        User storedUser = new User();
        BeanUtils.copyProperties(user, storedUser);
        storedUser.setId("xxxxx");

        //  Mock behaviours
        when(mockRepo.findByName("user")).thenReturn(Optional.of(storedUser));

        // Test (must throw exception)
        try {
            userService.update(user).get();
        } catch (Exception e) {
            if (!(e.getCause() instanceof UserException)) {
                Assert.fail();
            }
        }
    }

    /**
     * Test scenario where user is deleted by other user
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "root", authorities = {UserProfile.ADMIN})
    public void itDeletesUsers() throws Exception {
        User storedUser = new User();
        BeanUtils.copyProperties(user, storedUser);

        //  Mock behaviours
        when(mockRepo.findByName("user")).thenReturn(Optional.of(storedUser));

        // Test
        userService.delete("user").get();

        // Check mock iteration
        verify(mockRepo).delete(user);
    }

    /**
     * Test scenario where user try to deleted itself
     *
     * @throws Exception
     */
    @Test(expected = AccessDeniedException.class)
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itDeniesDeleteIfSelfDelete() throws Exception {
        User storedUser = new User();
        BeanUtils.copyProperties(user, storedUser);

        //  Mock behaviours
        when(mockRepo.findByName("user")).thenReturn(Optional.of(storedUser));

        // Test (must throw exception)
        userService.delete("user").get();
    }
}