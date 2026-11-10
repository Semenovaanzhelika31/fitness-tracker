package com.example.fittracker.ui.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittracker.FitTrackerApplication
import com.example.fittracker.data.model.Workout
import com.example.fittracker.data.prefs.UserSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val application = app as FitTrackerApplication
    private val repository = application.repository
    private val settings = application.settings

    private val currentUserId = settings.state
        .map { it.currentUserId }
        .distinctUntilChanged()

    /** true, если выбран текущий пользователь, для которого ведётся история. */
    val hasUser: StateFlow<Boolean> = currentUserId
        .map { it != UserSettings.NO_USER }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), settings.state.value.currentUserId != UserSettings.NO_USER)

    val workouts: StateFlow<List<Workout>> = currentUserId
        .flatMapLatest { id ->
            if (id == UserSettings.NO_USER) flowOf(emptyList()) else repository.workoutsOf(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val userId: Long get() = settings.state.value.currentUserId

    fun save(workout: Workout) {
        viewModelScope.launch { repository.save(workout) }
    }

    fun delete(workout: Workout) {
        viewModelScope.launch { repository.delete(workout) }
    }
}
