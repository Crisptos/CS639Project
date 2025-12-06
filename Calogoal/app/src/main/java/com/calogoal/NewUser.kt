package com.calogoal

import android.R
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NewUser(navController: NavController) {
    val context = LocalContext.current

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Welcome to Calogoal!",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Let's get you set up for success.",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Button(
                onClick = {
                    val prefs = context.getSharedPreferences("calogoal_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putBoolean("is_new_user", false).apply()

                    navController.navigate(Screen.TrendTracking.route) {
                        popUpTo(Screen.NewUser.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Get Started",
                style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
