package com.calogoal.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.calogoal.enums.TimeOfMeal

@Entity(tableName = "tracked_food")
data class TrackedFood(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val label: String,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbs: Int,
    val mealType: TimeOfMeal,

    val isCustom: Boolean = false
)