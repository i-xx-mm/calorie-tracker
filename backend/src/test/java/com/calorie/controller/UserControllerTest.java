package com.calorie.controller;

import com.calorie.dto.BMIResponse;
import com.calorie.dto.UserProfileDto;
import com.calorie.exception.ForbiddenException;
import com.calorie.exception.GlobalExceptionHandler;
import com.calorie.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup
        .MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;
    private final UserService userService =
            mock(UserService.class);
    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(userService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Authentication auth(String username) {
        return new UsernamePasswordAuthenticationToken(
                username, null, null
        );
    }

    @Test
    void getProfile_ownProfile_shouldReturn200()
            throws Exception {
        UserProfileDto dto = UserProfileDto.builder()
                .username("testuser")
                .age(26)
                .height(160)
                .build();

        when(userService.getProfile("testuser"))
                .thenReturn(dto);

        mockMvc.perform(
                        get("/api/users/testuser")
                                .principal(auth("testuser")))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.username").value("testuser")
                );
    }

    @Test
    void getProfile_otherUser_shouldThrowForbidden()
            throws Exception {
        mockMvc.perform(
                        get("/api/users/otheruser")
                                .principal(auth("testuser")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCurrentUserProfile_shouldReturn200()
            throws Exception {
        UserProfileDto dto = UserProfileDto.builder()
                .username("testuser")
                .build();

        when(userService.getProfile("testuser"))
                .thenReturn(dto);

        mockMvc.perform(
                        get("/api/users/me")
                                .principal(auth("testuser")))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.username").value("testuser")
                );
    }

    @Test
    void updateProfile_ownProfile_shouldReturn200()
            throws Exception {
        UserProfileDto dto = UserProfileDto.builder()
                .username("testuser")
                .height(165)
                .build();

        when(userService.updateProfile(
                eq("testuser"), any()))
                .thenReturn(dto);

        mockMvc.perform(
                        put("/api/users/testuser")
                                .principal(auth("testuser"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.height").value(165)
                );
    }

    @Test
    void getBMI_shouldReturn200() throws Exception {
        BMIResponse bmi = BMIResponse.builder()
                .username("testuser")
                .bmi(20.2)
                .category("Normal Weight")
                .build();

        when(userService.calculateBMI("testuser"))
                .thenReturn(bmi);

        mockMvc.perform(
                        get("/api/users/testuser/bmi")
                                .principal(auth("testuser")))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.category")
                                .value("Normal Weight")
                );
    }
}