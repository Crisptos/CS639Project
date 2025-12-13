package com.calogoal.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.calogoal.models.TrackedFood

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: TrackedFood)

    @Query("SELECT * FROM tracked_food")
    fun getAllTrackedFoods(): Flow<List<TrackedFood>>

    @Query("SELECT * FROM tracked_food WHERE isCustom = 1")
    fun getAllCustomFoods(): Flow<List<TrackedFood>>
}