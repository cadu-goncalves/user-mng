package com.creativedrive.user.control;

import com.creativedrive.user.domain.User;
import com.creativedrive.user.domain.UserFilter;
import com.creativedrive.user.domain.UserPage;
import com.creativedrive.user.domain.UserProfile;
import com.creativedrive.user.service.UserService;
import com.creativedrive.user.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Tests for {@link UserSearchController}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserSearchControllerTest {

    @MockBean
    private UserService mockService;

    @Autowired
    private UserSearchController controller;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    private UserFilter filter;

    private ObjectMapper mapper;

    @Before
    public void beforeEach() {
        // Fixtures
        mapper = new ObjectMapper();

        user = new User();
        user.setId("234242342");
        user.setName("user_name");
        user.setProfile(UserProfile.ADMIN);
        user.setPassword("pwd123");
        user.setEmail("user@email.com");

        filter = new UserFilter();
        filter.setFields(user);

        // Reset mocks
        reset(mockService);
    }

    /**
     * Test scenario where Request Body contains invalid data
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.USER})
    public void itRejectsInvalidPayload() throws Exception {
        // Invalid payload
        filter.setAsc(Sets.newHashSet("invalid"));
        filter.setDesc(Sets.newHashSet("invalid"));
        String payload = mapper.writeValueAsString(filter);

        // Expected messages (attached by CustomHandler)
        String msgPassword = MessageUtils.getMessage("messages", "filter.fields.asc.invalid");
        String msgProfile = MessageUtils.getMessage("messages", "filter.fields.desc.invalid");

        // Request
        MockHttpServletRequestBuilder reqBuilder = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        // Call & Check
        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(msgPassword)))
                .andExpect(content().string(containsString(msgProfile)));
    }


    /**
     * Test scenario for POST user filter
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.USER})
    public void itHandlesPostUserFilter() throws Exception {
         // Mock behaviours
        UserPage page = new UserPage(1, 0, Lists.newArrayList(user));
        when(mockService.findUsers(any(UserFilter.class))).thenReturn(CompletableFuture.completedFuture(page));

        // Request
        filter.setFields(new User());
        String payload = mapper.writeValueAsString(filter);
        MockHttpServletRequestBuilder reqBuilder = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        // Call
        MvcResult result = mockMvc.perform(reqBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();

        // Check
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(user.getId()))
                .andExpect(jsonPath("$.content[0].name").value(user.getName()))
                .andExpect(jsonPath("$.number").value(filter.getPage()))
                .andExpect(jsonPath("$.totalPages").value(page.getTotalPages()));

        // Check mock iteration
        verify(mockService).findUsers(any(UserFilter.class));
    }
}
