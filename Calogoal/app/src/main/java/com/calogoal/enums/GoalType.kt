package com.calogoal.enums

enum class GoalType(
    val label: String,
    val calorieAdjustment: Int
) {
    MAINTAIN("Maintain Weight", 0),
    LOSE("Lose Weight", -500),
    GAIN("Gain Weight", 300)
}
