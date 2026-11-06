package com.example.fittracker.ui.screens.workout

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittracker.R
import com.example.fittracker.ui.components.PlaceholderScreen

@Composable
fun WorkoutScreen(viewModel: WorkoutViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PlaceholderScreen(
        title = stringResource(R.string.workout_title),
        description = stringResource(R.string.workout_placeholder),
        icon = Icons.Filled.Timer
    )
}
