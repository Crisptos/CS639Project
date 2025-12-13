package com.calogoal.data.remote

import com.calogoal.data.remote.models.FoodApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query



interface EdamamApiService {
    @GET("api/food-database/v2/parser")
    suspend fun searchFoods(
        @Query("ingr") query: String,
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String
    ): Response<FoodApiResponse>
}