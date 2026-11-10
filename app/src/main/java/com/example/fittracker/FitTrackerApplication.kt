package com.example.fittracker

import android.app.Application
import com.example.fittracker.data.FitRepository
import com.example.fittracker.data.db.AppDatabase
import com.example.fittracker.data.prefs.UserSettings

/** Простой контейнер зависимостей приложения. */
class FitTrackerApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val repository: FitRepository by lazy { FitRepository(database) }
    val settings: UserSettings by lazy { UserSettings(this) }
}
