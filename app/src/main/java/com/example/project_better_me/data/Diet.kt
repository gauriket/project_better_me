package com.example.project_better_me.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diet")
data class Diet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,         // e.g., "Paneer Tikka"
    val mealType: String,     // Breakfast, Lunch, Dinner, Snack
    val calories: Int,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val date: Long = System.currentTimeMillis()
)
