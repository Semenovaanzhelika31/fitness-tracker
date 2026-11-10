package com.example.fittracker.ui.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.fittracker.FitTrackerApplication
import com.example.fittracker.data.model.Exercise
import com.example.fittracker.data.model.Workout
import com.example.fittracker.data.model.WorkoutExercise
import com.example.fittracker.data.model.WorkoutExerciseWithExercise
import com.example.fittracker.data.model.WorkoutSet
import com.example.fittracker.ui.navigation.WorkoutDetailRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutDetailViewModel(
    app: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(app) {
    private val repository = (app as FitTrackerApplication).repository
    private val workoutId: Long = savedStateHandle.get<Long>(WorkoutDetailRoute.ARG) ?: 0L

    val workout: StateFlow<Workout?> = repository.workout(workoutId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val items: StateFlow<List<WorkoutExerciseWithExercise>> = repository.workoutExercises(workoutId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val sets: StateFlow<List<WorkoutSet>> = repository.sets(workoutId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val exercises: StateFlow<List<Exercise>> = repository.exercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ---- упражнения тренировки ----
    fun addExercise(exercise: Exercise) {
        val nextOrder = (items.value.maxOfOrNull { it.workoutExercise.orderIndex } ?: 0) + 1
        viewModelScope.launch {
            repository.save(
                WorkoutExercise(workoutId = workoutId, exerciseId = exercise.id, orderIndex = nextOrder)
            )
        }
    }

    fun updateOrder(item: WorkoutExercise, orderIndex: Int) {
        viewModelScope.launch { repository.save(item.copy(orderIndex = orderIndex)) }
    }

    fun deleteExercise(item: WorkoutExercise) {
        viewModelScope.launch { repository.delete(item) }
    }

    // ---- подходы ----
    fun saveSet(set: WorkoutSet) {
        viewModelScope.launch { repository.save(set) }
    }

    fun deleteSet(set: WorkoutSet) {
        viewModelScope.launch { repository.delete(set) }
    }
}
