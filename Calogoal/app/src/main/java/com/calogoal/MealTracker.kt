package com.calogoal
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.calogoal.enums.TimeOfMeal
import com.calogoal.models.TrackedFood

@Composable
fun MealTracker(navController: NavController) {
    MealSection(title = TimeOfMeal.Breakfast, items = listOf(
        TrackedFood("Toast", 167, 5, 40, 2),
        TrackedFood("Eggs", 100, 8, 6, 0),
        TrackedFood("Orange", 55, 1, 0, 12),
        TrackedFood("Sausage", 212, 9, 18, 2),
        TrackedFood("Milk", 130, 8, 5, 14)
    ))
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