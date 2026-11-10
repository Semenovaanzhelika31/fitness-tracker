package com.example.fittracker.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fittracker.R

/**
 * Все экраны нижней навигации. Новые экраны добавляются сюда
 * и автоматически попадают в [bottomNavItems].
 */
sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    data object Exercises : Screen("exercises", R.string.nav_exercises, Icons.Filled.FitnessCenter)
    data object Workout : Screen("workout", R.string.nav_workout, Icons.Filled.Timer)
    data object History : Screen("history", R.string.nav_history, Icons.Filled.History)
    data object Profile : Screen("profile", R.string.nav_profile, Icons.Filled.Person)
}

val bottomNavItems: List<Screen> =
    listOf(Screen.Exercises, Screen.Workout, Screen.History, Screen.Profile)

/** Экран деталей тренировки (не входит в нижнюю панель). */
object WorkoutDetailRoute {
    const val ARG = "workoutId"
    const val PATTERN = "workout/{$ARG}"
    fun create(workoutId: Long) = "workout/$workoutId"
}
