package com.calogoal
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calogoal.services.FirestoreServiceImpl
import com.calogoal.viewmodels.LoginViewModel
import com.calogoal.viewmodels.ProfilePageViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val user =

            Calogoal(
                startDestination = Screen.Login.route
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
            val vm: ProfilePageViewModel = hiltViewModel()
            ProfilePage(
                navController = navController,
                viewModel = vm
            )
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
        composable(Screen.Login.route) {
            val vm: LoginViewModel = viewModel()
            LoginScreen(
                navController = navController,
                viewModel = vm
            )
        }
    }
}