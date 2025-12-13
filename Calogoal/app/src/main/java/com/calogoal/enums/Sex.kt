package com.calogoal.enums

enum class Sex(val value: Int, val label: String) {
    Male(0, "Male"),
    Female(1, "Female"),
    Nonbinary(2, "Non-Binary"),
    Other(3, "Other");

    companion object {
        private val map = Sex.entries.associateBy(Sex::value)
        fun fromInt(type: Int) = map[type] ?: Sex.Male
    }
}
