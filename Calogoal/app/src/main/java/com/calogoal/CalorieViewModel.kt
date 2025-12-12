package com.calogoal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.calogoal.enums.ExerciseRoutine
import com.calogoal.enums.Sex
import com.calogoal.models.MealTracker
import java.time.LocalDate

// All profile data we want to persist
data class Profile(
    val name: String = "",
    val sex: Sex = Sex.MALE,
    val dateOfBirth: LocalDate? = null,
    val heightInInches: String = "",
    val weightInLbs: String = "",
    val targetWeightInLbs: String = "",
    val exerciseRoutine: ExerciseRoutine = ExerciseRoutine.LITTLE_OR_NONE,
    val targetCalories: Int = 0,
    val goalType: String = "Maintain"
)

class CalorieViewModel : ViewModel() {

    var profile by mutableStateOf(Profile())
        private set

    private var mealIdCounter = 0

    var meals by mutableStateOf(listOf<MealTracker>())
        private set

    fun updateProfile(
        name: String,
        sex: Sex,
        dateOfBirth: LocalDate?,
        heightInInches: String,
        weightInLbs: String,
        targetWeightInLbs: String,
        exerciseRoutine: ExerciseRoutine,
        targetCalories: Int,
        goalType: String
    ) {
        profile = Profile(
            name = name,
            sex = sex,
            dateOfBirth = dateOfBirth,
            heightInInches = heightInInches,
            weightInLbs = weightInLbs,
            targetWeightInLbs = targetWeightInLbs,
            exerciseRoutine = exerciseRoutine,
            targetCalories = targetCalories,
            goalType = goalType
        )
    }

    // ----- Meal tracking -----

    fun addMeal(
        description: String,
        calories: Int,
        date: LocalDate = LocalDate.now()
    ) {
        if (description.isBlank() || calories <= 0) return

        val newMeal = MealTracker(
            id = mealIdCounter++,
            date = date,
            description = description,
            calories = calories
        )
        meals = meals + newMeal
    }

    fun mealsForDate(date: LocalDate): List<MealTracker> {
        return meals.filter { it.date == date }
    }

    fun dailyTotal(date: LocalDate): Int {
        return meals.filter { it.date == date }.sumOf { it.calories }
    }

    fun trend(days: Long = 7): List<Pair<LocalDate, Int>> {
        val today = LocalDate.now()
        val start = today.minusDays(days - 1)

        return (0 until days).map { offset ->
            val day = start.plusDays(offset)
            day to dailyTotal(day)
        }
    }
}
