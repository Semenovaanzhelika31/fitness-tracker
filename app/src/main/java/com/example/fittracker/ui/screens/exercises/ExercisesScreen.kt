package com.example.fittracker.ui.screens.exercises

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittracker.R
import com.example.fittracker.ui.components.PlaceholderScreen

@Composable
fun ExercisesScreen(viewModel: ExercisesViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PlaceholderScreen(
        title = stringResource(R.string.exercises_title),
        description = stringResource(R.string.exercises_placeholder),
        icon = Icons.Filled.FitnessCenter
    )
}
