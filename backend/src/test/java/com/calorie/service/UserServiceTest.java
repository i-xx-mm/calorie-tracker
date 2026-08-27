package com.calorie.service;

import com.calorie.dto.BMIResponse;
import com.calorie.dto.UserProfileDto;
import com.calorie.exception.ResourceNotFoundException;
import com.calorie.model.User;
import com.calorie.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    void calculateBMI_normalCase() {
        User user = new User();
        user.setUsername("test1");
        user.setHeight(160);
        user.setCurrentWeight(52.0);
        user.setAge(26);
        user.setGender("F");

        when(userRepository.findByUsername("test1")).thenReturn(Optional.of(user));
        BMIResponse resp = userService.calculateBMI("test1");
        assertEquals("Normal Weight", resp.getCategory());
    }

    @Test
    void calculateBMI_missingHeight_throw() {
        User user = new User();
        user.setUsername("test1");
        user.setHeight(null);
        user.setCurrentWeight(52.0);

        when(userRepository.findByUsername("test1")).thenReturn(Optional.of(user));
        assertThrows(ResourceNotFoundException.class, ()->userService.calculateBMI("test1"));
    }

    @Test
    void getGoalCalories_ok() {
        User user = new User();
        user.setUsername("test1");
        user.setHeight(160);
        user.setCurrentWeight(52.0);
        user.setAge(26);
        user.setGender("F");

        User spyUser = spy(user);
        when(userRepository.findByUsername("test1")).thenReturn(Optional.of(spyUser));
        when(spyUser.calculateGoalCalories()).thenReturn(1800);

        Integer goal = userService.getGoalCalories("test1");
        assertEquals(1800, goal);
    }

    @Test
    void updateProfile_ok() {
        User user = new User();
        user.setUsername("test1");
        user.setHeight(155);
        user.setCurrentWeight(55.0);
        user.setAge(25);
        user.setGender("F");

        when(userRepository.findByUsername("test1")).thenReturn(Optional.of(user));
        UserProfileDto dto = new UserProfileDto();
        dto.setHeight(160);
        dto.setAge(26);

        UserProfileDto out = userService.updateProfile("test1", dto);
        assertEquals(160, out.getHeight());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getProfile_userNotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->userService.getProfile("nouser"));
    }
}