package com.calogoal

sealed class Screen(val route: String) {
    object MealTracking : Screen("mealTracking")
    object Profile : Screen("profile")
    object TrendTracking : Screen("trendTracking")

    object NewUser : Screen("new_user")

}
