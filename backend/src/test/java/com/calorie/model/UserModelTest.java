package com.calorie.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    @Test
    void calculateGoalCalories_female_correctFormula() {
        User user = new User();
        user.setHeight(160);
        user.setCurrentWeight(52.0);
        user.setAge(26);
        user.setGender("female");

        Integer goal = user.calculateGoalCalories();

        // BMR = 10*52 + 6.25*160 - 5*26 - 161
        //     = 520 + 1000 - 130 - 161 = 1229
        // TDEE = 1229 * 1.55 = 1905
        assertTrue(goal > 1200); // above safety floor
        assertEquals(1905, goal);
    }

    @Test
    void calculateGoalCalories_male_correctFormula() {
        User user = new User();
        user.setHeight(175);
        user.setCurrentWeight(75.0);
        user.setAge(30);
        user.setGender("male");

        Integer goal = user.calculateGoalCalories();

        // BMR = 10*75 + 6.25*175 - 5*30 + 5
        //     = 750 + 1093.75 - 150 + 5 = 1698.75
        // TDEE = 1698.75 * 1.55 = 2633
        assertTrue(goal > 1200);
        assertEquals(2633, goal);
    }

    @Test
    void calculateGoalCalories_nullHeight_returns2000() {
        User user = new User();
        user.setHeight(null);
        user.setCurrentWeight(52.0);
        user.setAge(26);
        user.setGender("female");

        assertEquals(2000, user.calculateGoalCalories());
    }

    @Test
    void calculateGoalCalories_nullWeight_returns2000() {
        User user = new User();
        user.setHeight(160);
        user.setCurrentWeight(null);
        user.setAge(26);
        user.setGender("female");

        assertEquals(2000, user.calculateGoalCalories());
    }

    @Test
    void calculateGoalCalories_nullAge_returns2000() {
        User user = new User();
        user.setHeight(160);
        user.setCurrentWeight(52.0);
        user.setAge(null);
        user.setGender("female");

        assertEquals(2000, user.calculateGoalCalories());
    }

    @Test
    void calculateGoalCalories_otherGender_returns2000() {
        User user = new User();
        user.setHeight(160);
        user.setCurrentWeight(52.0);
        user.setAge(26);
        user.setGender("other");

        assertEquals(2000, user.calculateGoalCalories());
    }

    @Test
    void calculateGoalCalories_belowMinimum_returns1200() {
        // Very small person - could go below 1200
        User user = new User();
        user.setHeight(100);
        user.setCurrentWeight(20.0);
        user.setAge(120);
        user.setGender("female");

        Integer goal = user.calculateGoalCalories();
        assertTrue(goal >= 1200); // never below safety floor
    }
}
