package com.example.fittracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fittracker.ui.screens.exercises.ExercisesScreen
import com.example.fittracker.ui.screens.history.HistoryScreen
import com.example.fittracker.ui.screens.workout.WorkoutScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Exercises.route,
        modifier = modifier
    ) {
        composable(Screen.Exercises.route) { ExercisesScreen() }
        composable(Screen.Workout.route) { WorkoutScreen() }
        composable(Screen.History.route) { HistoryScreen() }
    }
}
