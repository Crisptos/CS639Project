package com.calogoal

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.calogoal.enums.TimeOfMeal
import com.calogoal.models.TrackedFood

@Composable
fun MealTracker(
    navController: NavController,
    viewModel: CalorieViewModel
) {
    val activity = LocalContext.current as? Activity

    // Local list of tracked foods for this screen
    var trackedFoods by remember {
        mutableStateOf(
            listOf(
                TrackedFood("Toast", 167, 5, 40, 2, TimeOfMeal.Breakfast),
                TrackedFood("Eggs", 100, 8, 6, 0, TimeOfMeal.Breakfast),
                TrackedFood("Orange", 55, 1, 0, 12, TimeOfMeal.Snack),
                TrackedFood("Sausage", 212, 9, 18, 2, TimeOfMeal.Breakfast),
                TrackedFood("Milk", 130, 8, 5, 14, TimeOfMeal.Breakfast)
            )
        )
    }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Page
                IconButton(onClick = {
                    navController.navigate(Screen.Profile.route) {
                        launchSingleTop = true
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = stringResource(R.string.cd_profile_page)
                    )
                }
                // Trend Tracking
                IconButton(onClick = {
                    navController.navigate(Screen.TrendTracking.route) {
                        launchSingleTop = true
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = stringResource(R.string.cd_trend_tracking)
                    )
                }
                // Exit App
                IconButton(onClick = { activity?.finish() }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.cd_exit_app)
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
            // Food Input Form
            FoodInputForm(
                onFoodAdded = { newFood ->
                    // Update local UI list
                    trackedFoods = trackedFoods + newFood
                    // Also record in shared ViewModel so Trend screen sees it
                    viewModel.addMeal(
                        description = newFood.label,
                        calories = newFood.calories
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Meal Tracking Display (all four sections)
            MealSection(
                title = TimeOfMeal.Breakfast,
                items = trackedFoods.filter { it.mealType == TimeOfMeal.Breakfast }
            )
            Spacer(modifier = Modifier.height(24.dp))

            MealSection(
                title = TimeOfMeal.Lunch,
                items = trackedFoods.filter { it.mealType == TimeOfMeal.Lunch }
            )
            Spacer(modifier = Modifier.height(24.dp))

            MealSection(
                title = TimeOfMeal.Dinner,
                items = trackedFoods.filter { it.mealType == TimeOfMeal.Dinner }
            )
            Spacer(modifier = Modifier.height(24.dp))

            MealSection(
                title = TimeOfMeal.Snack,
                items = trackedFoods.filter { it.mealType == TimeOfMeal.Snack }
            )
        }
    }
}

@Composable
fun FoodInputForm(onFoodAdded: (TrackedFood) -> Unit) {
    var foodNameInput by remember { mutableStateOf("") }
    var caloriesInput by remember { mutableStateOf("") }
    var selectedMeal by remember { mutableStateOf(TimeOfMeal.Breakfast) }
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = stringResource(R.string.add_food_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Food Name Input
        OutlinedTextField(
            value = foodNameInput,
            onValueChange = { foodNameInput = it },
            label = { Text(stringResource(R.string.label_food_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Calories Input
        OutlinedTextField(
            value = caloriesInput,
            onValueChange = { caloriesInput = it.filter { char -> char.isDigit() } },
            label = { Text(stringResource(R.string.label_calories)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Meal type dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.meal_type_label, selectedMeal.name))
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                TimeOfMeal.entries.forEach { meal ->
                    DropdownMenuItem(
                        text = { Text(meal.name) },
                        onClick = {
                            selectedMeal = meal
                            expanded = false
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
                    val newFood = TrackedFood(
                        label = name,
                        calories = calories,
                        protein = 0,
                        carbs = 0,
                        fat = 0,
                        mealType = selectedMeal
                    )
                    onFoodAdded(newFood)

                    foodNameInput = ""
                    caloriesInput = ""
                    selectedMeal = TimeOfMeal.Breakfast
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = foodNameInput.isNotEmpty() && caloriesInput.toIntOrNull() != null
        ) {
            Text(stringResource(R.string.button_add_food))
        }
    }
}

@Composable
fun MealSection(
    title: TimeOfMeal,
    items: List<TrackedFood>,
) {
    val totalCalories = items.sumOf { it.calories }
    val unitKcal = stringResource(R.string.unit_kcal)

    Column {
        Text(
            text = title.name,
            style = MaterialTheme.typography.titleMedium
        )

        items.forEach { e ->
            Text(
                text = stringResource(
                    R.string.food_item_display,
                    e.label,
                    e.calories,
                    unitKcal
                ),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        Text(
            text = stringResource(
                R.string.total_calories_summary,
                totalCalories,
                unitKcal
            ),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 12.dp, top = 2.dp)
        )
    }
}
