package com.calorie.service;

import com.calorie.exception.ConflictException;
import com.calorie.exception.ResourceNotFoundException;
import com.calorie.model.Food;
import com.calorie.repository.FoodRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {
    @Mock
    private FoodRepository foodRepository;
    @InjectMocks
    private FoodService foodService;

    @Test
    void searchFoods_returnList() {
        Food f = new Food();
        f.setName("apple");
        f.setCalorie(95);

        when(foodRepository.searchByName("apple")).thenReturn(List.of(f));
        List<Food> res = foodService.searchFoods(" Apple ");
        assertEquals(1, res.size());
    }

    @Test
    void getFoodById_found() {
        Food food = new Food();
        food.setId("1");
        food.setName("banana");
        food.setCalorie(105);

        when(foodRepository.findById("1")).thenReturn(Optional.of(food));
        Food res = foodService.getFoodById("1");
        assertEquals("banana", res.getName());
    }

    @Test
    void getFoodById_notFound_throw() {
        when(foodRepository.findById("99")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> foodService.getFoodById("99"));
    }

    @Test
    void createFood_success() {
        when(foodRepository.existsByNameAndCalorie("apple",95)).thenReturn(false);
        Food mockSaved = new Food();
        mockSaved.setName("apple");
        mockSaved.setCalorie(95);
        when(foodRepository.save(any())).thenReturn(mockSaved);

        Food out = foodService.createFood("Apple ",95);
        assertEquals("apple", out.getName());
    }

    @Test
    void createFood_duplicate_throwConflict() {
        when(foodRepository.existsByNameAndCalorie("apple",95)).thenReturn(true);
        assertThrows(ConflictException.class, () -> foodService.createFood("Apple",95));
    }

    @Test
    void getOrCreateFood_exist_returnExist() {
        Food exist = new Food();
        exist.setName("egg");
        exist.setCalorie(70);
        when(foodRepository.findByNameAndCalorie("egg",70)).thenReturn(Optional.of(exist));

        Food res = foodService.getOrCreateFood(" Egg ",70);
        verify(foodRepository,never()).save(any());
    }

    @Test
    void getOrCreateFood_notExist_createNew() {
        when(foodRepository.findByNameAndCalorie("egg",70)).thenReturn(Optional.empty());
        when(foodRepository.existsByNameAndCalorie("egg",70)).thenReturn(false);
        Food mockFood = new Food();
        mockFood.setName("egg");
        mockFood.setCalorie(70);
        when(foodRepository.save(any())).thenReturn(mockFood);

        Food res = foodService.getOrCreateFood("egg",70);
        assertNotNull(res);
        verify(foodRepository).save(any());
    }

    @Test
    void deleteFood_notExist_throw() {
        when(foodRepository.existsById("x")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,()->foodService.deleteFood("x"));
    }
}