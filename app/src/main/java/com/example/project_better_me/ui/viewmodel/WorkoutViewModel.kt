package com.example.project_better_me.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_better_me.data.Workout
import com.example.project_better_me.data.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {
    val workouts: StateFlow<List<Workout>> = repository.allWorkouts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.addWorkout(workout)
        }
    }

    fun removeWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.removeWorkout(workout)
        }
    }
}
