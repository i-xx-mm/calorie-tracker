package com.calorie.service;

import com.calorie.model.Food;
import com.calorie.repository.FoodRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
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
    void getOrCreateFood_exist_returnExist() {
        Food exist = new Food();
        exist.setName("egg");
        exist.setCalorie(70);

        when(foodRepository.findByNameAndCalorie("egg", 70))
                .thenReturn(Optional.of(exist));
        doReturn(exist).when(foodRepository).save(any(Food.class));

        foodService.getOrCreateFood(" Egg ", 70);

        verify(foodRepository, times(1)).save(any());
    }

    @Test
    void getOrCreateFood_notExist_createNew() {
        when(foodRepository.findByNameAndCalorie("egg",70)).thenReturn(Optional.empty());

        Food mockFood = new Food();
        mockFood.setName("egg");
        mockFood.setCalorie(70);
        when(foodRepository.save(any())).thenReturn(mockFood);

        Food res = foodService.getOrCreateFood("egg",70);
        assertNotNull(res);
        verify(foodRepository, times(1)).save(any());
    }
}