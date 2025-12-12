@file:OptIn(ExperimentalMaterial3Api::class)

package com.calogoal

import android.app.Activity
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.calogoal.enums.ExerciseRoutine
import com.calogoal.enums.GoalType
import com.calogoal.enums.Sex
import com.calogoal.ui.theme.CalogoalTheme
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.math.roundToInt

// Single definition of calculateBMR
fun calculateBMR(weightKg: Double, heightCm: Double, ageYears: Int, isMale: Boolean): Double {
    return if (isMale) {
        (10 * weightKg) + (6.25 * heightCm) - (5 * ageYears) + 5
    } else {
        (10 * weightKg) + (6.25 * heightCm) - (5 * ageYears) - 161
    }
}

@Composable
fun ProfilePage(
    navController: NavController,
    viewModel: CalorieViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Load previously saved profile from the ViewModel
    val savedProfile = viewModel.profile

    // Profile Data State (initialized from saved profile)
    var name by remember { mutableStateOf(savedProfile.name) }
    var sex by remember { mutableStateOf(savedProfile.sex) }

    var dateOfBirth by remember { mutableStateOf(savedProfile.dateOfBirth) }
    val dobFormatter = remember { DateTimeFormatter.ofPattern("MM/dd/yyyy") }
    val dobDisplayText = dateOfBirth?.format(dobFormatter) ?: "MM/DD/YYYY"

    var heightInInches by remember { mutableStateOf(savedProfile.heightInInches) }
    var weightInLbs by remember { mutableStateOf(savedProfile.weightInLbs) }
    var targetWeightInLbs by remember { mutableStateOf(savedProfile.targetWeightInLbs) }

    // Exercise routine from profile
    var selectedExerciseRoutine by remember { mutableStateOf(savedProfile.exerciseRoutine) }

    // Derived goal + calories
    val (inferredGoal, targetCaloriesPerDay) = remember(
        dateOfBirth,
        heightInInches,
        weightInLbs,
        targetWeightInLbs,
        sex,
        selectedExerciseRoutine
    ) {
        var goal = GoalType.MAINTAIN
        var calories: Int? = null

        val ageYears = dateOfBirth?.let { dob ->
            Period.between(dob, LocalDate.now()).years
        } ?: 0

        val heightInchesVal = heightInInches.toDoubleOrNull() ?: 0.0
        val currentWeight = weightInLbs.toDoubleOrNull() ?: 0.0
        val intendedWeight = targetWeightInLbs.toDoubleOrNull()

        // Infer goal from intended vs current weight (with small buffer)
        if (intendedWeight != null && currentWeight > 0.0) {
            goal = when {
                intendedWeight > currentWeight + 2 -> GoalType.GAIN
                intendedWeight < currentWeight - 2 -> GoalType.LOSE
                else -> GoalType.MAINTAIN
            }
        }

        if (ageYears > 0 && heightInchesVal > 0.0 && currentWeight > 0.0) {
            val weightKg = currentWeight * 0.453592
            val heightCm = heightInchesVal * 2.54

            val bmr = calculateBMR(weightKg, heightCm, ageYears, sex == Sex.MALE)
            val maintenanceCalories =
                (bmr * selectedExerciseRoutine.activityFactor).roundToInt()

            calories = (maintenanceCalories + goal.calorieAdjustment)
                .coerceAtLeast(1200) // sanity floor
        }

        goal to calories
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val greeting = stringResource(R.string.greeting)
                    Text(
                        text = if (name.isBlank()) greeting else "$greeting $name"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.secondary,
                )
            )
        },
        bottomBar = {
            // Bottom navigation bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navController.navigate(Screen.MealTracking.route)
                }) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Meal Tracking"
                    )
                }

                IconButton(onClick = {
                    navController.navigate(Screen.TrendTracking.route)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = "Trend Tracking"
                    )
                }

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
                .background(Color.White)
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar + Section Title
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GenderAvatar(isMale = sex == Sex.MALE)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Profile Data",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Profile Data Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // Name | Sex row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = {
                                    Text(
                                        "Name",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                modifier = Modifier.weight(2f),
                                singleLine = true
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Sex",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    FilterChip(
                                        selected = sex == Sex.MALE,
                                        onClick = { sex = Sex.MALE },
                                        label = { Text("M") }
                                    )
                                    FilterChip(
                                        selected = sex == Sex.FEMALE,
                                        onClick = { sex = Sex.FEMALE },
                                        label = { Text("F") }
                                    )
                                }
                            }
                        }

                        // Date of Birth | Height | Weight row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Date of Birth (MM/DD/YYYY, last 120 years)
                            Box(
                                modifier = Modifier.weight(1.3f)
                            ) {
                                OutlinedTextField(
                                    value = dobDisplayText,
                                    onValueChange = { /* read-only */ },
                                    label = {
                                        Text(
                                            "Date of Birth",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    enabled = false,
                                    readOnly = true,
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Clickable overlay to open DatePickerDialog
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable {
                                            val now = LocalDate.now()

                                            val cal = Calendar.getInstance()
                                            val maxDateMillis = cal.timeInMillis
                                            cal.add(Calendar.YEAR, -120)
                                            val minDateMillis = cal.timeInMillis

                                            val dialog = DatePickerDialog(
                                                context,
                                                { _, year, month, dayOfMonth ->
                                                    dateOfBirth = LocalDate.of(
                                                        year,
                                                        month + 1, // DatePickerDialog uses 0-based month
                                                        dayOfMonth
                                                    )
                                                },
                                                now.year,
                                                now.monthValue - 1,
                                                now.dayOfMonth
                                            )

                                            dialog.datePicker.maxDate = maxDateMillis
                                            dialog.datePicker.minDate = minDateMillis
                                            dialog.show()
                                        }
                                )
                            }

                            // Height (inches)
                            StatInputField(
                                value = heightInInches,
                                onValueChange = { heightInInches = it },
                                label = "Height (in)",
                                modifier = Modifier.weight(0.9f)
                            )

                            // Weight (lbs)
                            StatInputField(
                                value = weightInLbs,
                                onValueChange = { weightInLbs = it },
                                label = "Weight (lbs)",
                                modifier = Modifier.weight(0.9f)
                            )
                        }
                    }
                }
            }

            // Goals Section
            item {
                Text(
                    text = "Goals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Intended weight target
                        StatInputField(
                            value = targetWeightInLbs,
                            onValueChange = { targetWeightInLbs = it },
                            label = "Intended Weight Target (lbs)",
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Weekly Exercise Routine (enum-backed dropdown)
                        StringDropdownField(
                            label = "Weekly Exercise Routine",
                            options = ExerciseRoutine.values().map { it.label },
                            selectedOption = selectedExerciseRoutine.label,
                            onOptionSelected = { label ->
                                selectedExerciseRoutine =
                                    ExerciseRoutine.values().first { it.label == label }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Display inferred goal
                        Text(
                            text = "Goal: ${inferredGoal.label}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Target Calories Output
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Target calories per day",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = targetCaloriesPerDay?.let { "$it kcal" }
                                ?: "Enter your profile data and intended weight.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Save Button
            item {
                Button(
                    onClick = {
                        val target = targetCaloriesPerDay ?: return@Button
                        viewModel.updateProfile(
                            name = name,
                            sex = sex,
                            dateOfBirth = dateOfBirth,
                            heightInInches = heightInInches,
                            weightInLbs = weightInLbs,
                            targetWeightInLbs = targetWeightInLbs,
                            exerciseRoutine = selectedExerciseRoutine,
                            targetCalories = target,
                            goalType = inferredGoal.label
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    enabled = name.isNotBlank() && targetCaloriesPerDay != null
                ) {
                    Text("Save Profile")
                }
            }

            item {
                Spacer(Modifier.height(64.dp))
            }
        }
    }
}

// Reusable UI helpers (single definitions)

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
fun StringDropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val valueToShow = selectedOption.ifEmpty { "" }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = valueToShow,
            onValueChange = { /* read-only */ },
            readOnly = true,
            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Select $label"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun GenderAvatar(
    isMale: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = Color(0xFF3FB68E)  // Teal green
    val contentColor = Color.Black   // Best contrast on light green
    val emoji = if (isMale) "👨" else "👩"

    Box(
        modifier = modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineLarge,
            color = contentColor
        )
    }
}


@Preview(showBackground = true, name = "Profile Page Preview")
@Composable
fun ProfilePagePreview() {
    CalogoalTheme {
        ProfilePage(
            navController = rememberNavController(),
            viewModel = CalorieViewModel()
        )
    }
}
