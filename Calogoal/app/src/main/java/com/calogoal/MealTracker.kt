package com.calogoal

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.calogoal.enums.TimeOfMeal
import com.calogoal.models.TrackedFood

@Composable
fun MealTracker(navController: NavController) {
    val activity = LocalContext.current as? Activity

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1) Profile Page
                IconButton(onClick = {
                    navController.navigate(Screen.Profile.route) {
                        launchSingleTop = true
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile Page"
                    )
                }
                // 2) Trend Tracking
                IconButton(onClick = {
                    navController.navigate(Screen.TrendTracking.route) {
                        launchSingleTop = true
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = "Trend Tracking"
                    )
                }
                // 3) Exit App
                IconButton(onClick = { activity?.finish() }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Exit App"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            MealSection(title = TimeOfMeal.Breakfast, items = listOf(
                    TrackedFood("Toast", 167, 5, 40, 2),
                    TrackedFood("Eggs", 100, 8, 6, 0),
                    TrackedFood("Orange", 55, 1, 0, 12),
                    TrackedFood("Sausage", 212, 9, 18, 2),
                    TrackedFood("Milk", 130, 8, 5, 14)
                )
            )
        }
    }
}

@Composable
fun MealSection(
    title: TimeOfMeal,
    items: List<TrackedFood>,
) {
    Column {
        Text(
            text = title.name,
            style = MaterialTheme.typography.titleMedium
        )
        items.forEach {
                e ->
            Text(
                text = e.label + " | " + e.calories,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
            )
        }
        Text(
            text = "Calories: " + items.sumOf {e -> e.calories},
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 12.dp, top = 2.dp)
        )

    }
}