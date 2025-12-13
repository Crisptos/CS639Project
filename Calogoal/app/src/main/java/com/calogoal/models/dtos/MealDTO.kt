package com.calogoal.models.dtos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class MealDTO(
    val calories: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val label: String = "",
    val timeOfMealType: Int = 0,
    val protein: Int = 0,
    val dateEaten: Long = 0L,
)