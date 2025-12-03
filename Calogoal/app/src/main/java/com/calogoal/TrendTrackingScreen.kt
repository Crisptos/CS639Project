// Trend Tracking Screen

package com.calogoal

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@Composable
fun TrendTrackingScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CalorieViewModel
) {
    val profile = viewModel.profile
    val target = profile.targetCalories
    val goalType = profile.goalType
    val trend = viewModel.trend(days = 7)
    val formatter = DateTimeFormatter.ofPattern("dd MMM")

    // If no data available, show a message
    if (trend.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
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
            return
        }

    // Data available, show the chart and trend

        val totalDays = trend.size
        val daysGoalMet = trend.count { (_, calories) ->
        isGoalMet(goalType, calories, target)
    }
        val daysGoalNotMet = totalDays - daysGoalMet
        val startDate = trend.first().first.format(formatter)
        val endDate = trend.last().first.format(formatter)

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Calorie Trend Tracking",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Goal: $goalType • Target: $target kcal",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "From $startDate to $endDate",
                style = MaterialTheme.typography.bodyMedium
            )

            // Goal Status

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    tonalElevation = 2.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Days Goal Met", style = MaterialTheme.typography.labelMedium)
                        Text(
                            "$daysGoalMet / $totalDays",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Surface(
                    tonalElevation = 2.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Days Not Met", style = MaterialTheme.typography.labelMedium)
                        Text(
                            "$daysGoalNotMet / $totalDays",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

    // Chart of calories per day

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            factory = { context ->
                BarChart(context).apply {
                    description.isEnabled = false
                    axisRight.isEnabled = false
                    legend.isEnabled = true

                    axisLeft.axisMinimum = 0f
                    axisLeft.textColor = AndroidColor.DKGRAY

                    xAxis.granularity = 1f
                    xAxis.textColor = AndroidColor.DKGRAY
                    xAxis.setDrawGridLines(false)
                }
            },
            update = { chart ->
                val entries = trend.mapIndexed { index, (_, calories) ->
                    BarEntry(index.toFloat(), calories.toFloat())
                }

                val dataSet = BarDataSet(entries, "Daily Calories")

                // Set colors based on calorie levels
                // Green for calories <= target, Red for calories > target
                val colors = trend.map { (_, calories) ->
                    if (calories <= target) {
                        AndroidColor.parseColor("#4CAF50") // Green
                    } else {
                        AndroidColor.RED // Red
                    }
                }
                dataSet.colors = colors
                dataSet.valueTextColor = AndroidColor.DKGRAY
                dataSet.highLightColor = AndroidColor.BLACK

                chart.data = BarData(dataSet).apply {
                    barWidth = 0.6f
                }

                // Set target line
                chart.axisLeft.removeAllLimitLines()
                val targetLine = LimitLine(target.toFloat(), "Target ${target}kcal").apply {
                    lineWidth = 2f
                    lineColor = AndroidColor.BLUE
                    textColor = AndroidColor.BLUE
                    textSize = 10f
                }
                chart.axisLeft.addLimitLine(targetLine)

                chart.animateY(800)
                chart.invalidate()
            }
        )

        Divider()

    // Summary of calories per day
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        trend.forEach {
            (date, calories) ->
            val met = isGoalMet(goalType, calories, target)
            val status = if (met) "Goal met" else "Goal not met"
            Text("${date.format(formatter)} – $calories kcal ($status)")
        }
    }
}

private fun isGoalMet(goalType: String, calories: Int, target: Int): Boolean {
    return when (goalType) {
        "Gain Weight" -> calories >= target
        "Lose Weight" -> calories <= target
        "Maintain"    -> abs(calories - target) <= 250   // within +/- 250 kcal
        else          -> calories <= target
    }
}
