package com.memeals.meMealsApi.ScheduledMeal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.memeals.meMealsApi.Auth0.AuthService;
import com.memeals.meMealsApi.Exceptions.MealNotFoundException;
import com.memeals.meMealsApi.Exceptions.ScheduledMealNotFoundException;
import com.memeals.meMealsApi.IngredientMeal.IngredientMeal;
import com.memeals.meMealsApi.IngredientMeal.IngredientMealDTO;
import com.memeals.meMealsApi.Meal.Meal;
import com.memeals.meMealsApi.Meal.MealRepository;
import com.memeals.meMealsApi.User.User;


@Service
public class ScheduledMealServiceImpl implements ScheduledMealService {

    private final ScheduledMealRepository scheduledMealRepository;
    private final MealRepository mealRepository;
    private final AuthService authService;

    public ScheduledMealServiceImpl(ScheduledMealRepository scheduledMealRepository, 
                                     MealRepository mealRepository,
                                     AuthService authService) {
        this.authService = authService;
        this.scheduledMealRepository = scheduledMealRepository;
        this.mealRepository = mealRepository;
    }

    @Override
    public ScheduledMeal scheduleMeal(Long mealId, LocalDate date, ScheduledMealType mealType, Long userId) throws MealNotFoundException {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with id " + mealId));
        User User = authService.getUser();
        System.out.println("User IS FOUND : " + User.toString());
        ScheduledMeal scheduledMeal = new ScheduledMeal();
        scheduledMeal.setMeal(meal);
        scheduledMeal.setDate(date);
        scheduledMeal.setMealType(mealType);
        scheduledMeal.setUser(User);
        System.out.println("Scheduled Meal: " + scheduledMeal.toString());
        return scheduledMealRepository.save(scheduledMeal);
    }

    @Override
    public void deleteScheduledMeal(Long scheduledMealId) throws ScheduledMealNotFoundException {
        scheduledMealRepository.deleteById(scheduledMealId);
    }

    @Override
    public ScheduledMealDTO editScheduledMeal(Long scheduledMealId, LocalDate date, ScheduledMealType mealType) throws ScheduledMealNotFoundException {
        Optional<ScheduledMeal> optionalScheduledMeal = scheduledMealRepository.findById(scheduledMealId);
        if (!optionalScheduledMeal.isPresent()) {
            throw new ScheduledMealNotFoundException("Scheduled Meal not found for Id: " + scheduledMealId);
        }
        ScheduledMeal scheduledMeal = optionalScheduledMeal.get();
        scheduledMeal.setDate(date);
        scheduledMeal.setMealType(mealType);
        ScheduledMeal savedScheduledMeal = scheduledMealRepository.save(scheduledMeal);
        ScheduledMealDTO dto = new ScheduledMealDTO();
        dto.setId(savedScheduledMeal.getId());
        dto.setDate(savedScheduledMeal.getDate());
        dto.setMealType(savedScheduledMeal.getMealType());
        dto.setMealId(savedScheduledMeal.getMeal().getId());
        return dto;
    }

    @Override
    public List<ScheduledMealDTO> getAllMyScheduledMeal(Long userId) {
        List<ScheduledMeal> scheduledMeals = scheduledMealRepository.findByUserId(userId);
        List<ScheduledMealDTO> scheduledMealDTOs = new ArrayList<>();

        for (ScheduledMeal scheduledMeal : scheduledMeals) {
            ScheduledMealDTO dto = new ScheduledMealDTO();
            dto.setId(scheduledMeal.getId());
            dto.setDate(scheduledMeal.getDate());
            dto.setMealType(scheduledMeal.getMealType());
            dto.setMealId(scheduledMeal.getMeal().getId());
            scheduledMealDTOs.add(dto);
        }
        return scheduledMealDTOs;
        }

    @Override
    public List<IngredientMealDTO> getAllMyScheduledMealIngredients(Long userId, LocalDate startDate, LocalDate endDate) {
        List<ScheduledMeal> scheduledMeals = scheduledMealRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        List<IngredientMealDTO> mealIngredientDTOs = new ArrayList<>();
        for (ScheduledMeal scheduledMeal : scheduledMeals) {
            Meal meal = scheduledMeal.getMeal();
            List<IngredientMeal> mealIngredients = meal.getMealIngredients();
            for (IngredientMeal mealIngredient : mealIngredients) {
                mealIngredientDTOs.add(mealIngredient.toDTO());
            }
        }
        for (int i = 0; i < mealIngredientDTOs.size(); i++) {
            for (int j = i + 1; j < mealIngredientDTOs.size(); j++) {
                if (mealIngredientDTOs.get(i).getIngredientId().equals(mealIngredientDTOs.get(j).getIngredientId()) &&
                        mealIngredientDTOs.get(i).getUnitOfMeasurement().equals(mealIngredientDTOs.get(j).getUnitOfMeasurement()) &&
                        mealIngredientDTOs.get(i).getIngredientName().equals(mealIngredientDTOs.get(j).getIngredientName())) {
                    mealIngredientDTOs.get(i).setQuantity(mealIngredientDTOs.get(i).getQuantity() + mealIngredientDTOs.get(j).getQuantity());
                    mealIngredientDTOs.remove(j);
                    j--;
                }
            }
        }

        return mealIngredientDTOs;
    }
}
