package com.creativedrive.user.control;

import com.creativedrive.user.domain.CrudError;
import com.creativedrive.user.domain.User;
import com.creativedrive.user.domain.UserException;
import com.creativedrive.user.domain.UserProfile;
import com.creativedrive.user.service.UserService;
import com.creativedrive.user.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

/**
 * Tests for {@link UserCrudController}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserCrudControllerTest {

    @MockBean
    private UserService mockService;

    @Autowired
    private UserCrudController controller;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    private ObjectMapper mapper;

    @Before
    public void beforeEach() {
        // Fixtures
        mapper = new ObjectMapper();

        user = new User();
        user.setName("user_name");
        user.setProfile(UserProfile.ADMIN);
        user.setPassword("pwd123");
        user.setEmail("user@email.com");

        // Reset mocks
        reset(mockService);
    }

    /**
     * Test scenario where Request Body contains invalid data
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itRejectsInvalidPayload() throws Exception {
        // Invalid payload
        user.setPassword(null);
        user.setProfile("SUPER");
        user.setEmail("x");
        String payload = mapper.writeValueAsString(user);

        // Expected messages (attached by CustomHandler)
        String msgPassword = MessageUtils.getMessage("messages", "user.password.null");
        String msgProfile = MessageUtils.getMessage("messages", "user.profile.value");
        String msgEmail = MessageUtils.getMessage("messages", "user.email.format");

        // Request
        MockHttpServletRequestBuilder reqBuilder = post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        // Call & Check
        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(msgPassword)))
                .andExpect(content().string(containsString(msgProfile)))
                .andExpect(content().string(containsString(msgEmail)));
    }

    /**
     * Test scenario where non authorized (401) request occurs
     *
     * @throws Exception
     */
    @Test
    public void itUsesUnauthorizedAccessControl() throws Exception {
        // Mock behaviours
        when(mockService.create(user)).thenReturn(CompletableFuture.completedFuture(user));

        // Request
        MockHttpServletRequestBuilder reqBuilder = get("/api/user/{name}", user.getName());

        // Call && Check
        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test scenario for POST user
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itHandlesPostUser() throws Exception {
        // Mock behaviours
        when(mockService.create(user)).thenReturn(CompletableFuture.completedFuture(user));

        // Request
        String payload = mapper.writeValueAsString(user);
        MockHttpServletRequestBuilder reqBuilder = post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        // Call
        MvcResult result = mockMvc.perform(reqBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();

        // Check
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isCreated());

        // Check mock iteration
        verify(mockService).create(user);
    }

    /**
     * Test scenario for POST user error
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itHandlesPostUserError() throws Exception {
        // Mock behaviours
        final CompletableFuture<User> future = CompletableFuture.supplyAsync(() -> {
            throw new UserException("post error", CrudError.CREATE_ERROR);
        });
        when(mockService.create(user)).thenReturn(future);

        // Request
        String payload = mapper.writeValueAsString(user);
        MockHttpServletRequestBuilder reqBuilder = post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        // Call
        MvcResult result = mockMvc.perform(reqBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();

        // Check
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("post error")));

        // Check mock iteration
        verify(mockService).create(user);
    }

    /**
     * Test scenario for GET user
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.USER})
    public void itHandlesGetUser() throws Exception {
        // Mock behaviours
        when(mockService.retrieve(user.getName())).thenReturn(CompletableFuture.completedFuture(user));

        // Request
        MockHttpServletRequestBuilder reqBuilder = get("/api/user/{name}", user.getName());

        // Call
        MvcResult result = mockMvc.perform(reqBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();

        // Check
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()));

        // Check mock iteration
        verify(mockService).retrieve(user.getName());
    }

    /**
     * Test scenario for GET user error
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itHandlesGetUserError() throws Exception {
        // Mock behaviours
        final CompletableFuture<User> future = CompletableFuture.supplyAsync(() -> {
            throw new UserException("get error", CrudError.RETRIEVE_ERROR);
        });
        when(mockService.retrieve(user.getName())).thenReturn(future);

        // Request
        MockHttpServletRequestBuilder reqBuilder = get("/api/user/{name}", user.getName());

        // Call
        MvcResult result = mockMvc.perform(reqBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();

        // Check
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("get error")));

        // Check mock iteration
        verify(mockService).retrieve(user.getName());
    }

    /**
     * Test scenario for PUT user
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itHandlesPutUser() throws Exception {
        // Mock behaviours
        when(mockService.update(user)).thenReturn(CompletableFuture.completedFuture(user));

        // Request
        String payload = mapper.writeValueAsString(user);
        MockHttpServletRequestBuilder reqBuilder = put("/api/user/{name}", user.getName())
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
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()));

        // Check mock iteration
        verify(mockService).update(user);
    }

    /**
     * Test scenario for PUT user error
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itHandlesPutUserError() throws Exception {
        // Mock behaviours
        final CompletableFuture<User> future = CompletableFuture.supplyAsync(() -> {
            throw new UserException("put error", CrudError.UPDATE_ERROR);
        });
        when(mockService.update(user)).thenReturn(future);

        // Request
        String payload = mapper.writeValueAsString(user);
        MockHttpServletRequestBuilder reqBuilder = put("/api/user/{name}", user.getName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        // Call
        MvcResult result = mockMvc.perform(reqBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();

        // Check
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("put error")));

        // Check mock iteration
        verify(mockService).update(user);
    }

    /**
     * Test scenario for DELETE user
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itHandlesDeleteUser() throws Exception {
        // Mock behaviours
        when(mockService.delete(user.getName())).thenReturn(CompletableFuture.completedFuture(null));

        // Request
        MockHttpServletRequestBuilder reqBuilder = delete("/api/user/{name}", user.getName());

        // Call
        MvcResult result = mockMvc.perform(reqBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();

        // Check
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isOk());

        // Check mock iteration
        verify(mockService).delete(user.getName());
    }

    /**
     * Test scenario for DELETE user
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(authorities = {UserProfile.ADMIN})
    public void itHandlesDeleteUserError() throws Exception {
        // Mock behaviours
        final CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            throw new UserException("delete error", CrudError.DELETE_ERROR);
        });
        when(mockService.delete(user.getName())).thenReturn(future);

        // Request
        MockHttpServletRequestBuilder reqBuilder = delete("/api/user/{name}", user.getName());

        // Call
        MvcResult result = mockMvc.perform(reqBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();

        // Check
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("delete error")));

        // Check mock iteration
        verify(mockService).delete(user.getName());
    }

}