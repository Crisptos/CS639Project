package com.calogoal
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("calogoal_prefs", MODE_PRIVATE)
        val isNewUser = prefs.getBoolean("is_new_user", true)

        setContent {
            Calogoal(
                startDestination = if (isNewUser) Screen.NewUser.route else Screen.TrendTracking.route
            )
        }
    }
}

@Composable
fun Calogoal(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // New User Screen
        composable(Screen.NewUser.route) {
            NewUser(navController)
        }
        // Profile Screen
        composable(Screen.Profile.route) {
            ProfilePage(navController)
        }
        // Meal Tracking Screen
        composable(Screen.MealTracking.route) {
            MealTracker(navController)
        }
        // Trend Tracking Screen
        composable(Screen.TrendTracking.route) {
            val vm: CalorieViewModel = viewModel()
            TrendTrackingScreen(
                navController = navController,
                viewModel = vm
            )
        }
    }
}