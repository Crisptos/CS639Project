package com.calogoal.enums

enum class TimeOfMeal(val value: Int) {
    BREAKFAST(0),
    LUNCH(1),
    DINNER(2),
    SNACK(3),
    OTHER(99); // Use a distinct value for uncategorized/other

    // Companion object to help convert the integer value from Firestore back into the Enum
    companion object {
        private val map = entries.associateBy(TimeOfMeal::value)
        fun fromInt(type: Int) = map[type] ?: OTHER
    }
}
