package com.calogoal.enums

enum class ExerciseRoutine(
    val label: String,
    val activityFactor: Double
) {
    LITTLE_OR_NONE("Little or no exercise", 1.2),
    ONE_TO_THREE("1-3 days/week", 1.375),
    FOUR_TO_FIVE("4-5 days/week", 1.55),
    SIX_TO_SEVEN_INTENSE("6-7 days intense", 1.725)
}
