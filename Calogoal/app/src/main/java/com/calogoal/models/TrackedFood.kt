package com.calogoal.models

import com.calogoal.enums.TimeOfMeal
data class TrackedFood(
    val label: String,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbs: Int,
    val mealType: TimeOfMeal
)