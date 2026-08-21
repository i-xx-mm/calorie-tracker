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
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @Test
    void getFoodById_shouldReturnFood() throws Exception {
        Food food = new Food();
        food.setId("f1");
        food.setName("rice");
        food.setCalorie(130);

        when(foodService.getFoodById("f1")).thenReturn(food);

        mockMvc.perform(get("/api/foods/{id}", "f1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("f1"))
                .andExpect(jsonPath("$.name").value("rice"));
    }

    @Test
    void createFood_shouldReturnCreated() throws Exception {
        FoodRequest request = new FoodRequest();
        request.setName("egg");
        request.setCalorie(78);

        Food created = new Food();
        created.setId("f2");
        created.setName("egg");
        created.setCalorie(78);

        when(foodService.createFood("egg",78)).thenReturn(created);

        mockMvc.perform(post("/api/foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("f2"))
                .andExpect(jsonPath("$.name").value("egg"));
    }

    @Test
    void updateFood_shouldReturnUpdated() throws Exception {
        FoodRequest request = new FoodRequest();
        request.setName("egg updated");
        request.setCalorie(80);

        Food updated = new Food();
        updated.setId("f2");
        updated.setName("egg updated");
        updated.setCalorie(80);

        when(foodService.updateFood("f2","egg updated",80)).thenReturn(updated);

        mockMvc.perform(put("/api/foods/{id}","f2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("egg updated"));
    }

    @Test
    void deleteFood_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/foods/{id}","f2"))
                .andExpect(status().isNoContent());
    }
}