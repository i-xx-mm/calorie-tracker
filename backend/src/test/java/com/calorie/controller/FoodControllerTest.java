package com.calorie.controller;

import com.calorie.dto.FoodRequest;
import com.calorie.model.Food;
import com.calorie.service.FoodService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FoodControllerTest {

    private MockMvc mockMvc;
    private final FoodService foodService = mock(FoodService.class);

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FoodController(foodService)).build();
    }

    @Test
    void searchFoods_shouldReturnList() throws Exception {
        Food food = new Food();
        food.setId("f1");
        food.setName("chicken");
        food.setCalorie(165);
        List<Food> mockList = List.of(food);

        when(foodService.searchFoods(anyString())).thenReturn(mockList);

        mockMvc.perform(get("/api/foods")
                        .param("search", "chicken")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("f1"))
                .andExpect(jsonPath("$[0].name").value("chicken"));
    }
}