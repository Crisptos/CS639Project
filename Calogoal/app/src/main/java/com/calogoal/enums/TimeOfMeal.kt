package com.calogoal.enums

enum class TimeOfMeal(val value: Int) {
    Breakfast(0),
    Lunch(1),
    Dinner(2),
    Snack(3),
    Other(99); // Use a distinct value for uncategorized/other

    // Companion object to help convert the integer value from Firestore back into the Enum
    companion object {
        private val map = entries.associateBy(TimeOfMeal::value)
        fun fromInt(type: Int) = map[type] ?: TimeOfMeal.Other
    }
}
