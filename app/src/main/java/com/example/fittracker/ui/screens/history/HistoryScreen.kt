package com.example.fittracker.ui.screens.history

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittracker.R
import com.example.fittracker.ui.components.PlaceholderScreen

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PlaceholderScreen(
        title = stringResource(R.string.history_title),
        description = stringResource(R.string.history_placeholder),
        icon = Icons.Filled.History
    )
}
