package com.calogoal.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calogoal.enums.Sex
import com.calogoal.enums.TimeOfMeal
import com.calogoal.interfaces.FirestoreService
import com.calogoal.models.dtos.MealDTO
import com.calogoal.models.dtos.ProfileDTO
import com.calogoal.util.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TrackedMealUiState(
    val label: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
    val dateEaten: LocalDate? = null,
    val timeOfMealType: TimeOfMeal = TimeOfMeal.Other
)

data class MealTrackerUiState(
    var breakfast: List<TrackedMealUiState> = emptyList(),
    var lunch: List<TrackedMealUiState> = emptyList(),
    var dinner: List<TrackedMealUiState> = emptyList(),
    var snacks: List<TrackedMealUiState> = emptyList()
)

@HiltViewModel
class MealTrackerViewModel @Inject constructor(
    private val firestoreService: FirestoreService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealTrackerUiState())
    val uiState: StateFlow<MealTrackerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val data: List<MealDTO> = firestoreService.getMeals()
            val uiMeals = data.map { it.toUiState() }

            val groupedData = uiMeals.groupBy { it.timeOfMealType }

            _uiState.value = MealTrackerUiState(
                breakfast = groupedData[TimeOfMeal.Breakfast].orEmpty(),
                lunch = groupedData[TimeOfMeal.Lunch].orEmpty(),
                dinner = groupedData[TimeOfMeal.Dinner].orEmpty(),
                snacks = groupedData[TimeOfMeal.Snack].orEmpty()
            )

        }
    }

    fun addMeal(meal: TrackedMealUiState){
        when(meal.timeOfMealType){
            TimeOfMeal.Breakfast -> uiState.value.breakfast += meal
            TimeOfMeal.Lunch -> uiState.value.lunch += meal
            TimeOfMeal.Dinner -> uiState.value.dinner += meal
            TimeOfMeal.Snack -> uiState.value.snacks += meal
            TimeOfMeal.Other -> Log.e("Calogoal", "This switch statement shouldn't be reached something horrible happened")
        }
    }


}

private fun MealDTO.toUiState(): TrackedMealUiState =
    TrackedMealUiState(
        label = label,
        calories = calories,
        protein = protein,
        fat = fat,
        carbs = carbs,
        dateEaten = dateEaten.toLocalDate(),
        timeOfMealType = TimeOfMeal.fromInt(timeOfMealType)
    )