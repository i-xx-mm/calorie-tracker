package com.calorie.controller;

import com.calorie.dto.AddFoodLogRequest;
import com.calorie.dto.FoodLogDto;
import com.calorie.model.FoodItem;
import com.calorie.service.FoodLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup
        .MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.*;

class FoodLogControllerTest {

    private MockMvc mockMvc;
    private final FoodLogService foodLogService =
            mock(FoodLogService.class);
    private final ObjectMapper objectMapper =
            new ObjectMapper()
                    .registerModule(new JavaTimeModule());

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new FoodLogController(foodLogService)
                ).build();
    }

    private UsernamePasswordAuthenticationToken
    auth(String username) {
        return new UsernamePasswordAuthenticationToken(
                username, null, null
        );
    }

    @Test
    void getFoodLog_shouldReturn200() throws Exception {
        FoodLogDto dto = FoodLogDto.builder()
                .username("testuser")
                .date(LocalDate.of(2026, 8, 20))
                .foods(List.of())
                .totalCalories(0)
                .build();

        when(foodLogService.getFoodLogByDate(
                eq("testuser"), any()))
                .thenReturn(Optional.empty());
        when(foodLogService.getTotalCaloriesForDate(
                eq("testuser"), any()))
                .thenReturn(0);

        mockMvc.perform(
                        get("/api/foodlogs")
                                .param("date", "2026-08-20")
                                .principal(auth("testuser")))
                .andExpect(status().isOk());
    }

    @Test
    void addFoodEntry_shouldReturn201() throws Exception {
        AddFoodLogRequest request =
                new AddFoodLogRequest();
        request.setFoodName("apple");
        request.setCalorie(95);
        request.setDate("2026-08-20");

        com.calorie.model.FoodLog log =
                new com.calorie.model.FoodLog();
        log.setUsername("testuser");
        log.setFoods(new java.util.ArrayList<>());

        when(foodLogService.addFoodEntry(
                anyString(), any(), anyString(),
                anyInt(), any()))
                .thenReturn(log);
        when(foodLogService.getTotalCaloriesForDate(
                anyString(), any()))
                .thenReturn(95);

        mockMvc.perform(
                        post("/api/foodlogs")
                                .principal(auth("testuser"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                        .writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void addFoodEntry_emptyFoodName_shouldReturn400()
            throws Exception {
        AddFoodLogRequest request =
                new AddFoodLogRequest();
        request.setFoodName("");
        request.setCalorie(95);

        mockMvc.perform(
                        post("/api/foodlogs")
                                .principal(auth("testuser"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                        .writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteFoodEntry_shouldReturn200()
            throws Exception {
        com.calorie.model.FoodLog log =
                new com.calorie.model.FoodLog();
        log.setUsername("testuser");
        log.setFoods(new java.util.ArrayList<>());

        when(foodLogService.removeFoodEntry(
                anyString(), any(), anyInt()))
                .thenReturn(log);
        when(foodLogService.getTotalCaloriesForDate(
                anyString(), any()))
                .thenReturn(0);

        mockMvc.perform(
                        delete("/api/foodlogs/log123")
                                .param("index", "0")
                                .principal(auth("testuser")))
                .andExpect(status().isOk());
    }
}