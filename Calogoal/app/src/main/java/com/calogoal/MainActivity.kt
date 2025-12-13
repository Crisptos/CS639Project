package com.calogoal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.calogoal.ui.theme.CalogoalTheme
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calogoal.services.FirestoreServiceImpl
import com.calogoal.viewmodels.LoginViewModel
import com.calogoal.viewmodels.MealTrackerViewModel
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
    CalogoalTheme {
        val navController: NavHostController = rememberNavController()

        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
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
                    val vm: MealTrackerViewModel = hiltViewModel()
                    MealTracker(navController, vm)
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
        }
}
