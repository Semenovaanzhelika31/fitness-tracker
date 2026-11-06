package com.example.fittracker.ui.screens.exercises

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Состояние экрана списка упражнений (пока пустое, наполнится при подключении сети). */
data class ExercisesUiState(val isLoading: Boolean = false)

class ExercisesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ExercisesUiState())
    val uiState: StateFlow<ExercisesUiState> = _uiState.asStateFlow()
}
