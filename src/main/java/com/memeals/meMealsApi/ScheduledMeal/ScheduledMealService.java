package com.memeals.meMealsApi.ScheduledMeal;

import java.time.LocalDate;
import java.util.List;

import com.memeals.meMealsApi.IngredientMeal.IngredientMealDTO;

public interface ScheduledMealService {
    List<ScheduledMealDTO> getAllMyScheduledMeal(Long userId);
    ScheduledMeal scheduleMeal( Long mealId, LocalDate date, ScheduledMealType mealType, Long userId);
    void deleteScheduledMeal(Long scheduledMealId);
    ScheduledMealDTO editScheduledMeal(Long scheduledMealId, LocalDate date, ScheduledMealType mealType);
    List<IngredientMealDTO> getAllMyScheduledMealIngredients(Long userId, LocalDate startDate, LocalDate endDate);
}
