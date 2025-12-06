package com.calogoal.enums

enum class GoalType(val displayName: String) {
    GAIN_WEIGHT("Gain Weight"),
    LOSE_WEIGHT("Lose Weight"),
    MAINTAIN("Maintain");

    override fun toString(): String = displayName
}