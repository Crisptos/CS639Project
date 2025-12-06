package com.calogoal

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.calogoal.enums.TimeOfMeal
import com.calogoal.models.TrackedFood
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment


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
        ) { }
    }


    // 1. STATE MANAGEMENT: Initialize a mutable list to hold tracked foods.
    // In a real app, this would come from a ViewModel/Database.
    var trackedFoods by remember {
        mutableStateOf(
            listOf(
                TrackedFood("Toast", 167, 5, 40, 2, TimeOfMeal.Breakfast),
                TrackedFood("Eggs", 100, 8, 6, 0,TimeOfMeal.Breakfast),
                TrackedFood("Orange", 55, 1, 0, 12,TimeOfMeal.Snack),
                TrackedFood("Sausage", 212, 9, 18, 2,TimeOfMeal.Breakfast),
                TrackedFood("Milk", 130, 8, 5, 14,TimeOfMeal.Breakfast)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
    {
        // FOOD INPUT SECTION
        FoodInputForm(
            onFoodAdded = { newFood ->
                // Add the new food item to the list and trigger a recomposition
                trackedFoods = trackedFoods + newFood
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))

        //  MEAL TRACKING DISPLAY
        // Pass the dynamically updated list to MealSection
        MealSection(
            title = TimeOfMeal.Breakfast, // Hardcoded for simplicity
            items = trackedFoods
        )
    }
}

// New Composable for the input form
@Composable
fun FoodInputForm(onFoodAdded: (TrackedFood) -> Unit) {
    // Local state for input fields
    var foodNameInput by remember { mutableStateOf("") }
    var caloriesInput by remember { mutableStateOf("") }
    var selectedMeal by remember { mutableStateOf(TimeOfMeal.Breakfast) }
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Add New Food Item",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Food Name Input
        OutlinedTextField(
            value = foodNameInput,
            onValueChange = { foodNameInput = it },
            label = { Text("Food Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Calories Input
        OutlinedTextField(
            value = caloriesInput,
            onValueChange = { caloriesInput = it.filter { char -> char.isDigit() } },
            label = { Text("Calories (kcal)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Meal Type: ${selectedMeal.name}")
                // Add an icon if you like
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f) // Limit width of menu
            ) {
                TimeOfMeal.entries.forEach { meal ->
                    DropdownMenuItem(
                        text = { Text(meal.name) },
                        onClick = {
                            selectedMeal = meal // Update the state with the new meal type
                            expanded = false    // Close the menu
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Add Button
        Button(
            onClick = {
                val name = foodNameInput.trim()
                val calories = caloriesInput.toIntOrNull()

                if (name.isNotEmpty() && calories != null && calories > 0) {
                    // Create a new TrackedFood object
                    val newFood = TrackedFood(
                        label = name,
                        calories = calories,
                        protein = 0, // Simplified: Assume 0 for new input
                        carbs = 0,   // Simplified: Assume 0 for new input
                        fat = 0,
                        mealType = selectedMeal
                    )
                    onFoodAdded(newFood)

                    // Clear the input fields after successful addition
                    foodNameInput = ""
                    caloriesInput = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            // Disable button if inputs are empty/invalid
            enabled = foodNameInput.isNotEmpty() && caloriesInput.toIntOrNull() != null
        ) {
            Text("Add Food to Meal")
        }
    }
}

@Composable
fun MealSection(
    title: TimeOfMeal,
    items: List<TrackedFood>,
) {
    // Only calculate total calories, removing totalProtein, totalFat, and totalCarbs calculations.
    val totalCalories = items.sumOf { it.calories }

    Column {
        // 1. Display Meal Title
        Text(
            text = title.name,
            style = MaterialTheme.typography.titleMedium
        )

        // 2. List Individual Food Items with Calories
        items.forEach { e ->
            Text(
                // Display label and calories for a clean list
                text = "${e.label} | ${e.calories} kcal",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // 3. Display Total Summary (Only Calories)
        Text(
            // Show total Calories only
            text = "Total: $totalCalories kcal",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 12.dp, top = 2.dp)
        )

    }
}