package com.calogoal
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Calogoal()
        }
    }
}

@Composable
fun Calogoal() {
    val navController = rememberNavController()
    val vm: CalorieViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    NavHost(
        navController = navController,
        startDestination = "TrendTrackingScreen"
    ) {
        composable("MealTracking") { MealTracker(navController) }
        composable("TrendTrackingScreen"){TrendTrackingScreen(navController, viewModel = vm)}
    }
}