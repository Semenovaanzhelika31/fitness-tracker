package com.example.fittracker.ui.screens.exercises

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittracker.FitTrackerApplication
import com.example.fittracker.data.model.Exercise
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExercisesViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = (app as FitTrackerApplication).repository

    val exercises: StateFlow<List<Exercise>> = repository.exercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun save(exercise: Exercise) {
        viewModelScope.launch { repository.save(exercise) }
    }

    fun delete(exercise: Exercise) {
        viewModelScope.launch { repository.delete(exercise) }
    }
}
