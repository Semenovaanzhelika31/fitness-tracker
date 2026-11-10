package com.example.fittracker.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(val title: String) {
    SYSTEM("Системная"),
    LIGHT("Светлая"),
    DARK("Тёмная")
}

/** Настройки пользователя, хранящиеся в SharedPreferences. */
data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    /** id текущего пользователя или [UserSettings.NO_USER]. */
    val currentUserId: Long = UserSettings.NO_USER,
    /** Длительность обратного отсчёта перед подходом, секунд. */
    val countdownSeconds: Int = 3,
    val remindersEnabled: Boolean = false,
    val reminderHour: Int = 18,
    val reminderMinute: Int = 0
)

class UserSettings(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(read())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private fun read(): SettingsState = SettingsState(
        themeMode = runCatching { ThemeMode.valueOf(prefs.getString(KEY_THEME, "") ?: "") }
            .getOrDefault(ThemeMode.SYSTEM),
        currentUserId = prefs.getLong(KEY_CURRENT_USER, NO_USER),
        countdownSeconds = prefs.getInt(KEY_COUNTDOWN, 3),
        remindersEnabled = prefs.getBoolean(KEY_REMINDERS, false),
        reminderHour = prefs.getInt(KEY_REMINDER_HOUR, 18),
        reminderMinute = prefs.getInt(KEY_REMINDER_MINUTE, 0)
    )

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit { putString(KEY_THEME, mode.name) }
        _state.value = read()
    }

    fun setCurrentUser(userId: Long) {
        prefs.edit { putLong(KEY_CURRENT_USER, userId) }
        _state.value = read()
    }

    fun setCountdownSeconds(seconds: Int) {
        prefs.edit { putInt(KEY_COUNTDOWN, seconds) }
        _state.value = read()
    }

    fun setRemindersEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_REMINDERS, enabled) }
        _state.value = read()
    }

    fun setReminderTime(hour: Int, minute: Int) {
        prefs.edit {
            putInt(KEY_REMINDER_HOUR, hour)
            putInt(KEY_REMINDER_MINUTE, minute)
        }
        _state.value = read()
    }

    companion object {
        const val NO_USER = -1L
        private const val FILE_NAME = "user_settings"
        private const val KEY_THEME = "theme_mode"
        private const val KEY_CURRENT_USER = "current_user_id"
        private const val KEY_COUNTDOWN = "countdown_seconds"
        private const val KEY_REMINDERS = "reminders_enabled"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_MINUTE = "reminder_minute"
    }
}
