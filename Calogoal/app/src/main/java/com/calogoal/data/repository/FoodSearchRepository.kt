package com.calogoal.data.repository

import com.calogoal.data.remote.EdamamApiService
import com.calogoal.data.remote.models.FoodApiResponse
import retrofit2.Response // This import is already correct
import javax.inject.Inject
import javax.inject.Singleton

// We use hardcoded placeholder keys here.
// In a real app, you would inject these using BuildConfig fields or secrets.
private const val EDAMAM_APP_ID = "YOUR_APP_ID" // Replace with your key from gradle.properties
private const val EDAMAM_APP_KEY = "YOUR_APP_KEY" // Replace with your key from gradle.properties

@Singleton
class FoodSearchRepository @Inject constructor(
    private val apiService: EdamamApiService
) {
    // FIX: Changed return type to Response<FoodApiResponse> to match EdamamApiService
    suspend fun searchFood(query: String): Response<FoodApiResponse> {
        // This is the actual call to the Edamam API
        return apiService.searchFoods(
            query = query,
            appId = EDAMAM_APP_ID,
            appKey = EDAMAM_APP_KEY
        )
    }
}