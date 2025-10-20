package com.example.project_better_me.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

data class WorkoutExercise(
    val name: String,
    val caloriesPerHour: Double // from 155 lb column
)

fun loadExercisesFromCSV(context: Context, filename: String = "exercises.csv"): List<WorkoutExercise> {
    val exercises = mutableListOf<WorkoutExercise>()
    try {
        val inputStream = context.assets.open(filename)
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.useLines { lines ->
            lines.drop(1).forEach { line -> // skip header
                val cols = line.split(",")
                if (cols.size >= 3) { // Make sure 155 lb column exists
                    val name = cols[0].trim()
                    val calories = cols[2].trim().toDoubleOrNull() ?: 0.0 // 155 lb column
                    exercises.add(WorkoutExercise(name, calories))
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return exercises.sortedBy { it.name.lowercase() } // alphabetical order
}
