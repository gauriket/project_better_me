package com.example.project_better_me.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_better_me.data.Workout
import com.example.project_better_me.data.WorkoutDatabase
import com.example.project_better_me.data.WorkoutRepository
import com.example.project_better_me.ui.viewmodel.WorkoutViewModel
import com.example.project_better_me.ui.viewmodel.WorkoutViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import com.example.project_better_me.utils.loadExercisesFromCSV
import com.example.project_better_me.utils.WorkoutExercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen() {
    val context = LocalContext.current
    val exercises = remember { loadExercisesFromCSV(context) }

    var selectedExercise by remember { mutableStateOf<WorkoutExercise?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf("") } // minutes input

    var manualWorkoutName by remember { mutableStateOf("") }
    var manualCalories by remember { mutableStateOf("") }
    var isManual by remember { mutableStateOf(false) } // toggle manual input

    val db = WorkoutDatabase.getDatabase(context)
    val repo = WorkoutRepository(db.workoutDao())
    val viewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(repo))

    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }

    Column(modifier = Modifier.padding(16.dp)) {

        // Date selector
        DateSelector(selectedDate = selectedDate, onDateSelected = { selectedDate = it })

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle between dropdown and manual
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Manual Entry")
            Switch(
                checked = isManual,
                onCheckedChange = { isManual = it },
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isManual) {
            // Dropdown workout input
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedExercise?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Exercise") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    exercises.forEach { exercise ->
                        DropdownMenuItem(
                            text = { Text(exercise.name) },
                            onClick = {
                                selectedExercise = exercise
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it.filter { c -> c.isDigit() } },
                label = { Text("Duration (minutes)") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Manual workout input
            OutlinedTextField(
                value = manualWorkoutName,
                onValueChange = { manualWorkoutName = it },
                label = { Text("Workout Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = manualCalories,
                onValueChange = { manualCalories = it.filter { c -> c.isDigit() } },
                label = { Text("Calories Burned") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val workout = if (!isManual) {
                    val dur = duration.toDoubleOrNull() ?: 0.0
                    val calories = (selectedExercise?.caloriesPerHour ?: 0.0) * (dur / 60.0)
                    Workout(
                        name = selectedExercise?.name ?: "",
                        sets = 0,
                        reps = 0,
                        weight = 0,
                        date = selectedDate,
                        calories = calories.toInt()
                    )
                } else {
                    Workout(
                        name = manualWorkoutName,
                        sets = 0,
                        reps = 0,
                        weight = 0,
                        date = selectedDate,
                        calories = manualCalories.toIntOrNull() ?: 0
                    )
                }

                viewModel.addWorkout(workout)

                // Reset fields after adding
                selectedExercise = null
                duration = ""
                manualWorkoutName = ""
                manualCalories = ""
            },
            enabled = (!isManual && selectedExercise != null && duration.isNotBlank()) ||
                    (isManual && manualWorkoutName.isNotBlank() && manualCalories.isNotBlank())
        ) {
            Text("Add Workout")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show logged workouts
        val workouts by viewModel.workouts.collectAsState()
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

        val dailyWorkouts = workouts.filter { it.date in startOfDay..endOfDay }

        LazyColumn {
            items(dailyWorkouts) { workout ->
                WorkoutItem(workout, onDelete = { viewModel.removeWorkout(workout) })
            }
        }
    }
}

@Composable
fun DateSelector(selectedDate: Long, onDateSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    Button(onClick = {
        DatePickerDialog(context, { _, y, m, d ->
            val newCal = Calendar.getInstance()
            newCal.set(y, m, d, 0, 0, 0)
            onDateSelected(newCal.timeInMillis)
        }, year, month, day).show()
    }) {
        Text("Select Date")
    }
}

@Composable
fun WorkoutItem(workout: Workout, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(workout.name, style = MaterialTheme.typography.titleMedium)
                Text("Sets: ${workout.sets} | Reps: ${workout.reps} | Weight: ${workout.weight} kg")
                Text("🔥 Calories: ${workout.calories}")
            }

            IconButton(onClick = { onDelete() }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
