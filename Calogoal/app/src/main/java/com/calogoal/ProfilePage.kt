@file:OptIn(ExperimentalMaterial3Api::class)

package com.calogoal

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.calogoal.models.CalculatedCalories
import com.calogoal.viewmodels.ProfilePageViewModel
import kotlin.math.roundToInt

/**
 * Calculates Basal Metabolic Rate (BMR) using the Mifflin-St Jeor equation.
 */
fun calculateBMR(weightKg: Double, heightCm: Double, ageYears: Int, isMale: Boolean): Double {
    return if (isMale) {
        (10 * weightKg) + (6.25 * heightCm) - (5 * ageYears) + 5
    } else {
        (10 * weightKg) + (6.25 * heightCm) - (5 * ageYears) - 161
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(navController: NavController, viewModel: ProfilePageViewModel) {
    val activity = LocalContext.current as? Activity
    val uiState = viewModel.uiState.collectAsState().value

    // --- State Variables for User Input ---
    var isMale by remember { mutableStateOf(true) } // Simple gender toggle for BMR

    // --- State Variables for Goals List ---
    val goals = remember { mutableStateListOf("Lose 5 kg", "Drink 2L water daily") }
    var newGoalText by remember { mutableStateOf("") }

    // --- Derived State for Calorie Recommendation ---
    val calculatedCalories = remember(uiState.age, uiState.height, uiState.weight, isMale) {
        val ageVal = uiState.age
        val heightVal = uiState.height
        val weightVal = uiState.weight


        if (ageVal > 0 && heightVal > 0.0 && weightVal > 0.0) {
            val bmr = calculateBMR(weightVal.toDouble(), heightVal.toDouble(), ageVal, isMale)
            val sedentaryCalories = (bmr * 1.2).roundToInt()
            val oneToThreeCalories = (bmr * 1.375).roundToInt()
            val fourToFiveCalories = (bmr * 1.55).roundToInt()
            val sixToSevenIntenseCalories = (bmr * 1.725).roundToInt()

            CalculatedCalories(
                bmr = bmr.roundToInt(),
                sedentary = sedentaryCalories,
                oneToThree = oneToThreeCalories,
                fourToFive = fourToFiveCalories,
                intenseSixToSeven = sixToSevenIntenseCalories
            )
        } else {
            null
        }
    }

    val calculatedCalorieOutput = calculatedCalories?.let {
        stringResource(
            id = R.string.profile_info_calculated,
            it.bmr,
            it.sedentary,
            it.oneToThree,
            it.fourToFive,
            it.intenseSixToSeven
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.greeting) + " " + uiState.name) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        },
        bottomBar = {
            // ----- Bottom navigation bar -----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1) Calendar icon -> Meal Tracking
                IconButton(onClick = {
                    navController.navigate(Screen.MealTracking.route)
                }) {
                    Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = "Meal Tracking")
                }

                // 2) Chart icon -> Trend Tracking
                IconButton(onClick = {
                    navController.navigate(Screen.TrendTracking.route)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = "Trend Tracking"
                    )
                }

                // 3) X icon -> Exit app
                IconButton(onClick = { activity?.finish() }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Exit App"
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = stringResource(R.string.profile_header),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // --- 1. Basic Information Inputs ---
            item {
                Text(
                    text = stringResource(R.string.profile_field_basic_info),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { uiState.name = it },
                    label = { stringResource(R.string.name) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.gender), modifier = Modifier.weight(0.3f))
                    Row(
                        modifier = Modifier.weight(0.7f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = isMale,
                            onClick = { isMale = true },
                            label = { Text(stringResource(R.string.male)) }
                        )
                        FilterChip(
                            selected = !isMale,
                            onClick = { isMale = false },
                            label = { Text(stringResource(R.string.female)) }
                        )
                    }
                }
            }

            // --- 2. Input Stats (Age, Height, Weight) ---
            item {
                Text(
                    text = stringResource(R.string.profile_field_stats),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatInputField(
                        value = uiState.age.toString(),
                        onValueChange = { uiState.age = it.toInt() },
                        label = stringResource(R.string.age) + " (yrs)", // TODO change units in settings
                        modifier = Modifier.weight(1f)
                    )
                    StatInputField(
                        value = uiState.height.toString(),
                        onValueChange = { uiState.height = it.toInt() },
                        label = stringResource(R.string.height) + " (cm)",
                        modifier = Modifier.weight(1f)
                    )
                    StatInputField(
                        value = uiState.weight.toString(),
                        onValueChange = { uiState.weight = it.toInt() },
                        label = stringResource(R.string.weight) + "(kg)",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // --- 3. Calorie Recommendation Output ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.profile_label_calorie_rec),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = calculatedCalorieOutput ?: stringResource(R.string.profile_info_calculated_placeholder),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // --- 4. Journal Goals List ---
            item {
                Text(
                    "Journal Goals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newGoalText,
                        onValueChange = { newGoalText = it },
                        label = { Text( stringResource(R.string.profile_field_goal) ) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newGoalText.isNotBlank()) {
                                goals.add(newGoalText.trim())
                                newGoalText = ""
                            }
                        },
                        enabled = newGoalText.isNotBlank(),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Goal")
                    }
                }
            }

            // List of Current Goals
            itemsIndexed(goals) { index, goal ->
                GoalListItem(
                    goal = goal,
                    onDelete = { goals.removeAt(index) }
                )
            }

            item {
                Spacer(Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun StatInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                onValueChange(it)
            }
        },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun GoalListItem(goal: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = goal,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete Goal",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun CalogoalTheme(content: @Composable () -> Unit) {
    TODO("Not yet implemented")
}
