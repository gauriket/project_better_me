package com.example.project_better_me.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_better_me.data.*
import com.example.project_better_me.ui.viewmodel.DietViewModel
import com.example.project_better_me.ui.viewmodel.DietViewModelFactory
import com.example.project_better_me.ui.viewmodel.WorkoutViewModel
import com.example.project_better_me.ui.viewmodel.WorkoutViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen() {
    val context = LocalContext.current

    // --- Workout setup ---
    val workoutDb = WorkoutDatabase.getDatabase(context)
    val workoutRepo = WorkoutRepository(workoutDb.workoutDao())
    val workoutVM: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(workoutRepo))

    // --- Diet setup ---
    val dietDb = DietDatabase.getDatabase(context)
    val dietRepo = DietRepository(dietDb.dietDao())
    val dietVM: DietViewModel = viewModel(factory = DietViewModelFactory(dietRepo))

    // Selected date
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }

    // Start and end of day
    val startOfDay = Calendar.getInstance().apply {
        timeInMillis = selectedDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val endOfDay = Calendar.getInstance().apply {
        timeInMillis = selectedDate
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    val workouts by workoutVM.workouts.collectAsState()
    val dailyWorkouts = workouts.filter {
        it.date in startOfDay..endOfDay
    }
    val totalWorkoutCalories = dailyWorkouts.sumOf { it.calories }

    val dietMeals by dietVM.getDietForDate(startOfDay, endOfDay).collectAsState()
    val totalDietCalories = dietMeals.sumOf { it.calories }

    val netCalories = totalDietCalories - totalWorkoutCalories

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        // 📅 Date selector
        DateSelector(selectedDate = selectedDate, onDateSelected = { selectedDate = it })

        Spacer(modifier = Modifier.height(16.dp))

        // 📊 Summary section
        Text(
            "Summary for ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(selectedDate))}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("🍴 Total Calories Consumed: $totalDietCalories kcal")
        Text("🏋️ Total Calories Burnt: $totalWorkoutCalories kcal")
        Text(
            "⚖️ Net Calories: $netCalories kcal",
            color = if (netCalories >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🍽️ Meals Section
        Text("📜 Meals", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (dietMeals.isEmpty()) {
            Text("No meals logged for this day.")
        } else {
            Column {
                dietMeals.forEach { meal ->
                    DietItem(meal, onDelete = { dietVM.removeDiet(meal) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🏋️ Workouts Section
        Text("🏋️ Workouts", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (dailyWorkouts.isEmpty()) {
            Text("No workouts logged for this day.")
        } else {
            Column {
                dailyWorkouts.forEach { workout ->
                    WorkoutItem(workout, onDelete = { workoutVM.removeWorkout(workout) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
