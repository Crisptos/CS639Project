package com.calogoal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.calogoal.models.MealTracker
import java.time.LocalDate

data class Profile(
    val name: String = "",
    val targetCalories: Int = 2000,
    val goalType: String = "Lose Weight"
)

// ViewModel
class CalorieViewModel : ViewModel() {

    var profile by mutableStateOf(Profile())
        private set

    private var mealIdCounter = 0

    var meals by mutableStateOf(listOf<MealTracker>())
        private set

    fun updateProfile(
        name: String,
        targetCalories: Int,
        goalType: String
    ) {
        profile = profile.copy(
            name = name,
            targetCalories = targetCalories,
            goalType = goalType
        )
    }

    // MealTracker
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