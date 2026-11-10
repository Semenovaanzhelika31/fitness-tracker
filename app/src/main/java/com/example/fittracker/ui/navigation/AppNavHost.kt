package com.example.fittracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fittracker.ui.screens.exercises.ExercisesScreen
import com.example.fittracker.ui.screens.history.HistoryScreen
import com.example.fittracker.ui.screens.history.WorkoutDetailScreen
import com.example.fittracker.ui.screens.profile.ProfileScreen
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
        composable(Screen.History.route) {
            HistoryScreen(
                onOpenWorkout = { id -> navController.navigate(WorkoutDetailRoute.create(id)) }
            )
        }
        composable(Screen.Profile.route) { ProfileScreen() }
        composable(
            route = WorkoutDetailRoute.PATTERN,
            arguments = listOf(navArgument(WorkoutDetailRoute.ARG) { type = NavType.LongType })
        ) {
            WorkoutDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
