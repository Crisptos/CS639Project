package com.calogoal.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// @JsonClass is used by Moshi for code generation/optimization
@JsonClass(generateAdapter = true)
data class FoodApiResponse(

    val hints: List<Hint>,

    // The text input used for the search (optional, but good for debugging)
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Hint(
    // The actual Food object containing nutrition data and name
    val food: Food
)

@JsonClass(generateAdapter = true)
data class Food(
    // The common name of the food (e.g., "chicken breast")
    val label: String,

    // The unique food ID in the Edamam database
    @field:Json(name = "foodId") // Use @field:Json to map JSON key if different
    val foodId: String,

    // The category of the food (e.g., "Generic foods")
    val category: String,

    // The nutrition summary for 100g of the food
    val nutrients: Nutrients
)

@JsonClass(generateAdapter = true)
data class Nutrients(
    @field:Json(name = "ENERC_KCAL") // Total energy in kilocalories
    val calories: Double? = null,

    @field:Json(name = "PROCNT") // Protein in grams
    val protein: Double? = null,

    @field:Json(name = "FAT") // Fat in grams
    val fat: Double? = null,

    @field:Json(name = "CHOCDF") // Carbohydrates (Net) in grams
    val carbs: Double? = null
)