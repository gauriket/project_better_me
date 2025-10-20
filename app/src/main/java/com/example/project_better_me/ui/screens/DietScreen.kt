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
import com.example.project_better_me.data.Diet
import com.example.project_better_me.data.DietDatabase
import com.example.project_better_me.data.DietRepository
import com.example.project_better_me.ui.viewmodel.DietViewModel
import com.example.project_better_me.ui.viewmodel.DietViewModelFactory
import com.example.project_better_me.utils.loadIndianFoodsFromCSV
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun DietScreen() {
    val context = LocalContext.current
    val indianFoodMap = remember { loadIndianFoodsFromCSV(context) }
    val database = DietDatabase.getDatabase(context)
    val repository = DietRepository(database.dietDao())
    val viewModel: DietViewModel = viewModel(factory = DietViewModelFactory(repository))

    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var mealType by remember { mutableStateOf("Breakfast") }
    var servings by remember { mutableStateOf("1") }

    // Toggle between dropdown and manual
    var manualEntry by remember { mutableStateOf(false) }
    var selectedFood by remember { mutableStateOf(indianFoodMap.firstOrNull()) }
    var manualName by remember { mutableStateOf("") }
    var manualCalories by remember { mutableStateOf("") }
    var manualProtein by remember { mutableStateOf("") }
    var manualCarbs by remember { mutableStateOf("") }
    var manualFat by remember { mutableStateOf("") }

    // State for meals of selected date
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

    val meals by viewModel.getDietForDate(startOfDay, endOfDay).collectAsState()
    val totalCalories = meals.sumOf { it.calories }
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)) {

            // Date selector
            DateSelector(selectedDate = selectedDate, onDateSelected = { selectedDate = it })

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Diet for ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(selectedDate))}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "🔥 Total Calories: $totalCalories kcal",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Meal Type dropdown
            var mealExpanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { mealExpanded = true }) {
                    Text(mealType)
                }
                DropdownMenu(expanded = mealExpanded, onDismissRequest = { mealExpanded = false }) {
                    listOf("Breakfast", "Lunch", "Dinner", "Snack").forEach { type ->
                        DropdownMenuItem(text = { Text(type) }, onClick = {
                            mealType = type
                            mealExpanded = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Toggle between dropdown and manual
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Manual Entry")
                Switch(checked = manualEntry, onCheckedChange = { manualEntry = it })
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!manualEntry) {
                // Dropdown selection
                var foodExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { foodExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(selectedFood?.name ?: "Select Food")
                    }
                    DropdownMenu(
                        expanded = foodExpanded,
                        onDismissRequest = { foodExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        indianFoodMap.forEach { food ->
                            DropdownMenuItem(
                                text = { Text(food.name) },
                                onClick = {
                                    selectedFood = food
                                    foodExpanded = false
                                }
                            )
                        }
                    }
                }
            } else {
                // Manual entry fields
                OutlinedTextField(
                    value = manualName,
                    onValueChange = { manualName = it },
                    label = { Text("Food Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = manualCalories,
                    onValueChange = { manualCalories = it.filter { c -> c.isDigit() } },
                    label = { Text("Calories per serving") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = manualProtein,
                    onValueChange = { manualProtein = it.filter { c -> c.isDigit() } },
                    label = { Text("Protein per serving (g)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = manualCarbs,
                    onValueChange = { manualCarbs = it.filter { c -> c.isDigit() } },
                    label = { Text("Carbs per serving (g)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = manualFat,
                    onValueChange = { manualFat = it.filter { c -> c.isDigit() } },
                    label = { Text("Fat per serving (g)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Servings input
            OutlinedTextField(
                value = servings,
                onValueChange = { servings = it.filter { c -> c.isDigit() } },
                label = { Text("Servings") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                val servingCount = servings.toIntOrNull() ?: 1
                if (!manualEntry) {
                    selectedFood?.let { food ->
                        val diet = Diet(
                            name = food.name,
                            mealType = mealType,
                            calories = (food.calories * servingCount),
                            protein = (food.protein * servingCount),
                            carbs = (food.carbs * servingCount),
                            fat = (food.fat * servingCount),
                            date = selectedDate
                        )
                        viewModel.addDiet(diet)
                    }
                } else {
                    val diet = Diet(
                        name = manualName.ifBlank { "Manual Food" },
                        mealType = mealType,
                        calories = (manualCalories.toIntOrNull() ?: 0) * servingCount,
                        protein = (manualProtein.toDoubleOrNull() ?: 0.0) * servingCount,
                        carbs = (manualCarbs.toDoubleOrNull() ?: 0.0) * servingCount,
                        fat = (manualFat.toDoubleOrNull() ?: 0.0) * servingCount,
                        date = selectedDate
                    )
                    viewModel.addDiet(diet)

                    // Reset manual fields
                    manualName = ""
                    manualCalories = ""
                    manualProtein = ""
                    manualCarbs = ""
                    manualFat = ""
                }

                // Reset common fields
                servings = "1"
                selectedFood = indianFoodMap.firstOrNull()
            }) {
                Text("Add Meal")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Meal list
            Text("📜 Meals", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Meals list scrollable
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(meals) { meal ->
                DietItem(meal, onDelete = { viewModel.removeDiet(meal) })
            }
        }
    }
}

@Composable
fun DietItem(meal: Diet, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${meal.name} (${meal.mealType})", style = MaterialTheme.typography.titleMedium)
                Text("Calories: ${meal.calories} | P: ${meal.protein}g | C: ${meal.carbs}g | F: ${meal.fat}g")
            }
            IconButton(onClick = { onDelete() }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
