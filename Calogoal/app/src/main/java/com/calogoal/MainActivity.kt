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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalogoalApp()
        }
    }
}

@Composable
fun CalogoalApp() {
    CalogoalTheme {
        val navController: NavHostController = rememberNavController()
        // One shared ViewModel for the whole app
        val viewModel: CalorieViewModel = viewModel()

        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Profile.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Profile.route) {
                    ProfilePage(
                        navController = navController,
                        viewModel = viewModel
                    )
                }

                composable(Screen.MealTracking.route) {
                    MealTracker(
                        navController = navController,
                        viewModel = viewModel
                    )
                }

                composable(Screen.TrendTracking.route) {
                    TrendTrackingScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
