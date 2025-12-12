// Trend Tracking Screen

package com.calogoal

import android.app.Activity
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.calogoal.enums.GoalType
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

// Time-based greeting
private fun greetingMessage(): String {
    val hour = java.time.LocalTime.now().hour
    return when (hour) {
        in 5..11 -> "Good morning!"
        in 12..16 -> "Good afternoon!"
        else -> "Good evening!"
    }
}

/**
 * Returns a 7–day trend:
 *  - 6 previous days are hard–coded sample values
 *  - Today's calories come from the ViewModel's meals
 */
private fun getSevenDayTrend(viewModel: CalorieViewModel): List<Pair<LocalDate, Int>> {
    val today = LocalDate.now()

    // Hard-coded values for the previous 6 days (relative to today)
    // You can tweak these numbers as you like.
    val hardcodedMap = mapOf(
        today.minusDays(6) to 1850,
        today.minusDays(5) to 2000,
        today.minusDays(4) to 1725,
        today.minusDays(3) to 2100,
        today.minusDays(2) to 1950,
        today.minusDays(1) to 2300
    )

    // Build the list in chronological order: 6 days ago ... yesterday
    val previousDays = (6L downTo 1L).map { offset ->
        val date = today.minusDays(offset)
        date to (hardcodedMap[date] ?: 0)
    }

    // Today's real total from MealTracker / ViewModel
    val todayTotal = viewModel.dailyTotal(today)
    val todayEntry = today to todayTotal

    return previousDays + todayEntry
}

@Composable
fun TrendTrackingScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CalorieViewModel
) {
    val activity = LocalContext.current as? Activity

    val profile = viewModel.profile
    val target = profile.targetCalories

    val goalType: GoalType = when (profile.goalType) {
        "Gain Weight" -> GoalType.GAIN
        "Lose Weight" -> GoalType.LOSE
        "Maintain" -> GoalType.MAINTAIN
        else -> GoalType.MAINTAIN
    }

    // Initials for avatar
    val initials = remember(profile.name) {
        profile.name
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it[0].uppercase() }
            .ifEmpty { "S" }
    }

    // --- 7-day trend: 6 days hard-coded + today from ViewModel ---
    val trend = remember(viewModel.meals, profile.targetCalories) {
        getSevenDayTrend(viewModel)
    }

    val dayFormatter = DateTimeFormatter.ofPattern("dd")
    val monthFormatter = DateTimeFormatter.ofPattern("LLLL yyyy")

    Scaffold(
        bottomBar = {
            // Bottom navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                IconButton(onClick = {
                    navController.navigate(Screen.MealTracking.route) {
                        launchSingleTop = true
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Meal Tracking"
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
        if (trend.isEmpty()) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Calorie Trend Tracking",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "No data available",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            return@Scaffold
        }

        // ---- Weekly summary (7 days total) ----
        val totalDays = trend.size  // should be 7
        val daysGoalMet = trend.count { (_, calories) ->
            isGoalMet(goalType, calories, target)
        }
        val daysGoalNotMet = totalDays - daysGoalMet

        // Selected day (default: last day, i.e., today)
        var selectedIndex by remember { mutableStateOf(trend.lastIndex) }
        val selectedEntry = trend.getOrNull(selectedIndex) ?: trend.last()
        val selectedDate = selectedEntry.first
        val todayCalories = selectedEntry.second

        // Weekly progress background color
        val weeklyBgColor = when {
            daysGoalMet >= 5 -> Color(0xFFDDF9B5) // green
            daysGoalMet in 3..4 -> Color(0xFFFFF3CD) // yellow
            else -> Color(0xFFFFE5E5) // red
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Top header (avatar + greeting)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF222222)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = greetingMessage(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Weekly Progress card
            Surface(
                color = weeklyBgColor,
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Daily intake",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF4B6B34)
                        )
                        Text(
                            text = "Your Weekly\nProgress",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box(
                        modifier = Modifier.size(90.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .padding(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFB5E77D)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = daysGoalMet.toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "days",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Month and Week strip
            Surface(
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Month + prev / next day buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedDate.format(monthFormatter),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                            // ← previous day
                            Surface(
                                shape = CircleShape,
                                tonalElevation = 1.dp,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        if (selectedIndex > 0) {
                                            selectedIndex--
                                        }
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("←")
                                }
                            }

                            // → next day
                            Surface(
                                shape = CircleShape,
                                tonalElevation = 1.dp,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        if (selectedIndex < trend.lastIndex) {
                                            selectedIndex++
                                        }
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("→")
                                }
                            }
                        }
                    }

                    // Strip of 7 days (Mon/Tue/... + day of month)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        trend.forEachIndexed { index, (date, _) ->
                            val isSelected = index == selectedIndex
                            Column(
                                modifier = Modifier.clickable {
                                    selectedIndex = index
                                },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = date.dayOfWeek.name.first().toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            if (isSelected) Color(0xFFDDF9B5)
                                            else Color.Transparent
                                        )
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = date.format(dayFormatter),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else null
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Calories Card and bar chart
            Surface(
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header: today's calories + target
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Calories",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = todayCalories.toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Kcal",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Target:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (target > 0) "$target Kcal" else "Not set",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // MPAndroidChart BarChart
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        factory = { context ->
                            BarChart(context).apply {
                                description.isEnabled = false
                                setDrawGridBackground(false)
                                axisRight.isEnabled = false
                                legend.isEnabled = false

                                axisLeft.axisMinimum = 0f
                                axisLeft.axisMaximum = 120f
                                axisLeft.textColor = AndroidColor.DKGRAY
                                axisLeft.setDrawGridLines(false)

                                xAxis.position = XAxis.XAxisPosition.BOTTOM
                                xAxis.granularity = 1f
                                xAxis.textColor = AndroidColor.DKGRAY
                                xAxis.setDrawGridLines(false)
                            }
                        },
                        update = { chart ->
                            val entries = trend.mapIndexed { index, (_, calories) ->
                                val percent = if (target > 0) {
                                    (calories.toFloat() / target.toFloat()) * 100f
                                } else 0f
                                BarEntry(index.toFloat(), percent)
                            }

                            val dataSet = BarDataSet(entries, "Percent").apply {
                                val barColors = entries.mapIndexed { index, _ ->
                                    if (index == selectedIndex) {
                                        AndroidColor.parseColor("#7DD321") // selected green
                                    } else {
                                        AndroidColor.parseColor("#D7F2B8") // pale green
                                    }
                                }
                                setColors(barColors)
                                valueTextColor = AndroidColor.DKGRAY
                                valueTextSize = 10f
                                valueFormatter = PercentValueFormatter()
                            }

                            val barData = BarData(dataSet).apply {
                                barWidth = 0.5f
                            }

                            chart.data = barData

                            val labels = trend.map { (date, _) ->
                                date.dayOfWeek.name.substring(0, 3)
                            }
                            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

                            chart.axisLeft.removeAllLimitLines()
                            val targetLine =
                                LimitLine(100f, "100%").apply {
                                    lineWidth = 1.5f
                                    lineColor = AndroidColor.LTGRAY
                                    textColor = AndroidColor.LTGRAY
                                    textSize = 10f
                                }
                            chart.axisLeft.addLimitLine(targetLine)

                            chart.highlightValue(selectedIndex.toFloat(), 0)

                            chart.setFitBars(true)
                            chart.animateY(700)
                            chart.invalidate()
                        }
                    )

                    HorizontalDivider()

                    // Summary row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Days goal met: $daysGoalMet",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Days not met: $daysGoalNotMet",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

// Formatter to show "110%" style labels above bars
class PercentValueFormatter : ValueFormatter() {
    override fun getBarLabel(barEntry: BarEntry?): String {
        if (barEntry == null) return ""
        return "${barEntry.y.toInt()}%"
    }
}

/**
 * Goal evaluation uses GoalType enum:
 * - GAIN: calories >= target
 * - LOSE: calories <= target
 * - MAINTAIN: within ±250 kcal
 */
private fun isGoalMet(goalType: GoalType, calories: Int, target: Int): Boolean {
    if (target <= 0) return false

    return when (goalType) {
        GoalType.GAIN -> calories >= target
        GoalType.LOSE -> calories <= target
        GoalType.MAINTAIN -> abs(calories - target) <= 250
    }
}
