package com.calogoal.models

import java.time.LocalDate

data class MealTracker(
    val id: Int,
    val date: LocalDate,
    val description: String,
    val calories: Int
)
