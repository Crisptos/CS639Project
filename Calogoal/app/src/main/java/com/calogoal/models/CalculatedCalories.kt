package com.calogoal.models

data class CalculatedCalories (
    val bmr:               Int,
    val sedentary:         Int,
    val oneToThree:        Int,
    val fourToFive:        Int,
    val intenseSixToSeven: Int
)