package com.creativedrive.user.control;

import com.creativedrive.user.domain.User;
import com.creativedrive.user.domain.UserProfile;
import com.creativedrive.user.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User controller tests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private User user;

    private ObjectMapper mapper;

    @Before
    public void beforeEach() {
        // Fixtures
        mapper = new ObjectMapper();

        user = new User();
        user.setName("user");
        user.setProfile(UserProfile.ADMIN);
        user.setPassword("pwd123");
        user.setEmail("user@email.com");
    }

    /**
     * Test scenario where POST contains invalid email
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itValidatesPostInput() throws Exception {
        // Invalid payload
        user.setPassword(null);
        user.setProfile("SUPER");
        user.setEmail("x");
        String payload = mapper.writeValueAsString(user);

        // Expected messages
        String msgPassword = MessageUtils.getMessage("messages", "user.password.null");
        String msgProfile = MessageUtils.getMessage("messages", "user.profile.value");
        String msgEmail = MessageUtils.getMessage("messages", "user.email.format");

        // Request
        this.mockMvc.perform(
            post("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload)
        )
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(content().string(containsString(msgPassword)))
        .andExpect(content().string(containsString(msgEmail)));
    }
}