package com.calogoal.data

import com.calogoal.data.local.FoodDao
import com.calogoal.data.remote.EdamamApiService
import com.calogoal.models.TrackedFood
import com.calogoal.BuildConfig
import kotlinx.coroutines.flow.Flow

class FoodRepository(
    private val foodDao: FoodDao,
    private val apiService: EdamamApiService
) {
    // This now returns the corrected list of TrackedFood objects
    val trackedFoods: Flow<List<TrackedFood>> = foodDao.getAllTrackedFoods()

    suspend fun insertTrackedFood(food: TrackedFood) {
        foodDao.insertFood(food)
    }

    suspend fun searchFood(query: String): List<TrackedFood> {
        // Use BuildConfig for security (defined in Gradle steps)
        val appId = BuildConfig.EDAMAM_APP_ID
        val appKey = BuildConfig.EDAMAM_APP_KEY

        // This is simplified to compile; you will restore the API call later
        return emptyList()
    }
}