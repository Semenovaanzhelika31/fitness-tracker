package com.example.fittracker.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittracker.FitTrackerApplication
import com.example.fittracker.data.model.User
import com.example.fittracker.data.prefs.SettingsState
import com.example.fittracker.data.prefs.ThemeMode
import com.example.fittracker.data.prefs.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(app: Application) : AndroidViewModel(app) {
    private val application = app as FitTrackerApplication
    private val repository = application.repository
    private val settingsStore = application.settings

    val users: StateFlow<List<User>> = repository.users
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val settings: StateFlow<SettingsState> = settingsStore.state

    fun saveUser(user: User) {
        viewModelScope.launch {
            val id = repository.save(user)
            // Первый созданный пользователь автоматически становится текущим.
            if (settingsStore.state.value.currentUserId == UserSettings.NO_USER) {
                settingsStore.setCurrentUser(id)
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.delete(user)
            if (settingsStore.state.value.currentUserId == user.id) {
                settingsStore.setCurrentUser(UserSettings.NO_USER)
            }
        }
    }

    fun selectUser(user: User) = settingsStore.setCurrentUser(user.id)
    fun setThemeMode(mode: ThemeMode) = settingsStore.setThemeMode(mode)
    fun setCountdownSeconds(seconds: Int) = settingsStore.setCountdownSeconds(seconds)
    fun setRemindersEnabled(enabled: Boolean) = settingsStore.setRemindersEnabled(enabled)
    fun setReminderTime(hour: Int, minute: Int) = settingsStore.setReminderTime(hour, minute)
}
