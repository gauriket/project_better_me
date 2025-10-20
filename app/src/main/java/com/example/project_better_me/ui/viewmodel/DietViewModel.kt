package com.example.project_better_me.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_better_me.data.Diet
import com.example.project_better_me.data.DietRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DietViewModel(private val repository: DietRepository) : ViewModel() {

    fun addDiet(diet: Diet) = viewModelScope.launch {
        repository.addDiet(diet)
    }

    fun getDietForDate(start: Long, end: Long): StateFlow<List<Diet>> =
        repository.getDietForDate(start, end)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun removeDiet(diet: Diet) = viewModelScope.launch {
        repository.removeDiet(diet)
    }
}

class DietViewModelFactory(private val repository: DietRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DietViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DietViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
