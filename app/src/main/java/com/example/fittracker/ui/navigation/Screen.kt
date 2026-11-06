package com.example.fittracker.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
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
}

val bottomNavItems: List<Screen> = listOf(Screen.Exercises, Screen.Workout, Screen.History)
