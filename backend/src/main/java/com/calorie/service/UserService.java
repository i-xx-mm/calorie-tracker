package com.calorie.service;

import com.calorie.dto.BMIResponse;
import com.calorie.dto.UserProfileDto;
import com.calorie.exception.ResourceNotFoundException;
import com.calorie.model.User;
import com.calorie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for User profile related business logic
 * Handles profile query, partial update, goal calorie calculation, and BMI computation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Fetch complete User profile mapped to DTO
     * @param username target username
     * @return UserProfileDto containing user profile and computed daily goal calories
     * @throws ResourceNotFoundException when user does not exist
     */
    public UserProfileDto getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDto(user);
    }

    /**
     * Calculate daily goal calorie for given user
     * @param username target username
     * @return computed goal calorie
     * @throws ResourceNotFoundException when user does not exist
     */
    public Integer getGoalCalories(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.calculateGoalCalories();
    }

    /**
     * Partially update User profile, null fields will be skipped
     *
     * @param username target username
     * @param profileDto incoming update payload
     * @return UserProfileDto that was updated
     * @throws ResourceNotFoundException when user does not exist
     */
    public UserProfileDto updateProfile(String username, UserProfileDto profileDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (profileDto.getAge() != null) {
            user.setAge(profileDto.getAge());
        }
        if (profileDto.getHeight() != null) {
            user.setHeight(profileDto.getHeight());
        }
        if (profileDto.getGender() != null) {
            user.setGender(profileDto.getGender());
        }
        if (profileDto.getCurrentWeight() != null) {
            user.setCurrentWeight(profileDto.getCurrentWeight());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return mapToDto(user);
    }

    /**
     * Compute BMI for specified user
     * Requires both height and weight to be present on the User profile
     *
     * @param username target username
     * @return BMIResponse containing BMI value, category, and health status
     * @throws ResourceNotFoundException when user does not exist or height/weight field is missing on User profile
     */
    public BMIResponse calculateBMI(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getHeight() == null || user.getCurrentWeight() == null) {
            throw new ResourceNotFoundException("Height and weight data is required");
        }

        double heightM = user.getHeight() / 100.0;
        double bmi = user.getCurrentWeight() / (heightM * heightM);
        bmi = Math.round(bmi * 10.0) / 10.0;

        String category = getBMICategory(bmi);
        String status = getBMIStatus(bmi);

        return BMIResponse.builder()
                .username(username)
                .height(user.getHeight())
                .weight(user.getCurrentWeight())
                .bmi(bmi)
                .category(category)
                .status(status)
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Derive human-readable BMI category based on BMI value
     *
     * @param bmi bmi value
     * @return category string: Underweight/Normal Weight/Overweight/Obese
     */
    private String getBMICategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal Weight";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    /**
     * Determine health status based on BMI category string
     *
     * @param bmi bmi value
     * @return Healthy for Normal Weight(25>bmi>=18.5), otherwise Unhealthy
     */
    private String getBMIStatus(double bmi) {
        return (bmi >= 18.5 && bmi < 25) ? "Healthy" : "Unhealthy";
    }

    /**
     * Map User to UserProfileDto
     * @param user User entity
     * @return UserProfileDto fully contructed
     */
    private UserProfileDto mapToDto(User user) {
        Integer goalCalories = user.calculateGoalCalories();

        return UserProfileDto.builder()
                .username(user.getUsername())
                .age(user.getAge())
                .height(user.getHeight())
                .gender(user.getGender())
                .currentWeight(user.getCurrentWeight())
                .goalCalories(goalCalories)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}