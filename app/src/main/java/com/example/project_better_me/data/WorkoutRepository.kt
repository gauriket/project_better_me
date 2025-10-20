package com.example.project_better_me.data

import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val dao: WorkoutDao) {
    val allWorkouts: Flow<List<Workout>> = dao.getAllWorkouts()

    suspend fun addWorkout(workout: Workout) {
        dao.insertWorkout(workout)
    }

    suspend fun removeWorkout(workout: Workout) {
        dao.deleteWorkout(workout)
    }
}
