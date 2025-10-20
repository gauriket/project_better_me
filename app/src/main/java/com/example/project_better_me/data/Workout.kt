package com.example.project_better_me.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val sets: Int,
    val reps: Int,
    val weight: Int,
    val date: Long,
    val calories: Int = 0 // default 0, so old entries won’t break
)
