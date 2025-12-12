package com.calogoal.interfaces

import com.calogoal.models.dtos.MealDTO
import com.calogoal.models.dtos.ProfileDTO
import com.calogoal.viewmodels.ProfileUiState
import kotlinx.coroutines.flow.Flow

interface FirestoreService {
    fun setUserData(data: Map<String, Any>);
    fun updateUserData(data: Map<String, Any>);
    suspend fun getName(): String;
    suspend fun getProfile(): ProfileDTO
    suspend fun setProfile(profile: ProfileDTO)
    suspend fun getMeals(): List<MealDTO>
    suspend fun addMeals(meals: List<MealDTO>)
    suspend fun addMeal(meal: MealDTO)
}